package op.air.airclient.features.module.modules.music

import javazoom.jl.decoder.JavaLayerException
import javazoom.jl.player.AudioDevice
import javazoom.jl.player.JavaSoundAudioDevice
import javazoom.jl.player.Player
import op.air.airclient.config.ListValue
import op.air.airclient.event.GameTickEvent
import op.air.airclient.event.handler
import op.air.airclient.features.module.Category
import op.air.airclient.features.module.Module
import op.air.airclient.file.FileManager
import op.air.airclient.utils.client.ClientUtils
import op.air.airclient.utils.client.chat
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.util.concurrent.ConcurrentHashMap
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.FloatControl
import kotlin.concurrent.thread

object MusicPlayer : Module("MusicPlayer", Category.MUSIC) {

    private var volumeValue by int("音量", 50, 0..100)
    private val autoPlay by boolean("自动播放", false)
    private val loopMode by choices("循环模式", arrayOf("关闭", "单曲循环", "列表循环"), "列表循环")
    private val showInfo by boolean("显示信息", true)

    private var selectedMusicName = "无"
    
    private val musicDir: File by lazy {
        val musicPath = File(FileManager.dir, "Music")
        if (!musicPath.exists()) {
            musicPath.mkdirs()
        }
        musicPath
    }

    private var currentPlayer: Player? = null
    private var currentMusicFile: File? = null
    private var isPlaying = false
    private var playThread: Thread? = null
    private var currentAudioDevice: VolumeControlledAudioDevice? = null

    private val musicList = mutableListOf<File>()
    private var currentIndex = 0
    private val musicCache = ConcurrentHashMap<String, Long>()

    private var currentLyric: String = ""
    private var lyricLines = listOf<String>()
    private var currentLyricIndex = 0
    private var playStartTime: Long = 0
    private var lyricTimestamps = mutableListOf<Long>()
    
    private var musicDuration: Long = 0
    private var pausedTime: Long = 0

    val currentMusicName: String
        get() = currentMusicFile?.nameWithoutExtension ?: "无"

    val currentLyricDisplay: String
        get() = currentLyric

    val previousLyricDisplay: String
        get() = if (currentLyricIndex > 0 && lyricLines.isNotEmpty()) lyricLines[currentLyricIndex - 1] else ""

    val nextLyricDisplay: String
        get() = if (currentLyricIndex < lyricLines.size - 1 && lyricLines.isNotEmpty()) lyricLines[currentLyricIndex + 1] else ""

    val isCurrentlyPlaying: Boolean
        get() = isPlaying && currentPlayer != null

    val musicListNames: List<String>
        get() = musicList.map { it.nameWithoutExtension }

    private lateinit var musicChoicesValue: ListValue

    private fun initMusicChoices() {
        musicChoicesValue = choices("音乐", arrayOf("无"), "无").onChanged {
            if (it != "无" && it != selectedMusicName) {
                selectedMusicName = it
                val index = musicListNames.indexOf(it)
                if (index >= 0) {
                    playByIndex(index)
                }
            }
        } as ListValue
    }

    init {
        initMusicChoices()
    }
    
    private fun getMusicDuration(file: File): Long {
        return try {
            val audioInputStream = AudioSystem.getAudioInputStream(file)
            val format = audioInputStream.format
            val frames = audioInputStream.frameLength
            audioInputStream.close()
            if (frames > 0 && format.frameRate > 0) {
                (frames * 1000L / format.frameRate).toLong()
            } else {
                val fileSize = file.length()
                val bitRate = 128000
                (fileSize * 8L / bitRate * 1000L)
            }
        } catch (e: Exception) {
            try {
                val fileSize = file.length()
                val bitRate = 128000
                (fileSize * 8L / bitRate * 1000L)
            } catch (e2: Exception) {
                0L
            }
        }
    }
    
    val progress: Float
        get() {
            if (!isPlaying || currentPlayer == null || musicDuration <= 0) return 0F
            val elapsed = System.currentTimeMillis() - playStartTime
            return (elapsed.toFloat() / musicDuration.toFloat()).coerceIn(0F, 1F)
        }
    
    val currentTimeString: String
        get() {
            if (!isPlaying || currentPlayer == null) return "0:00"
            val elapsed = (System.currentTimeMillis() - playStartTime) / 1000
            val minutes = elapsed / 60
            val seconds = elapsed % 60
            return "$minutes:${seconds.toString().padStart(2, '0')}"
        }
    
    val totalTimeString: String
        get() {
            if (musicDuration <= 0) return "0:00"
            val totalSeconds = musicDuration / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return "$minutes:${seconds.toString().padStart(2, '0')}"
        }
    
    val timeDisplayString: String
        get() = "$currentTimeString / $totalTimeString"

    override fun onEnable() {
        super.onEnable()
        scanMusicFiles()
        
        if (musicList.isEmpty()) {
            chat("§c[音乐播放器] 未找到音乐文件！请将音乐文件放入: ${musicDir.absolutePath}")
            return
        }

        if (showInfo) {
            chat("§a[音乐播放器] 已加载 ${musicList.size} 首音乐，可以在HUD编辑界面中加入歌词显示")
            chat("§7[音乐播放器] 音乐目录: ${musicDir.absolutePath}")
        }

        if (selectedMusicName != "无" && musicListNames.contains(selectedMusicName)) {
            val index = musicListNames.indexOf(selectedMusicName)
            if (index >= 0) {
                playByIndex(index)
                return
            }
        }

        if (autoPlay && musicList.isNotEmpty()) {
            playMusic(musicList[0])
        }
    }

    override fun onDisable() {
        super.onDisable()
        stopMusic()
        if (showInfo) {
            chat("§c[音乐播放器] 已停止播放")
        }
    }

    val onTick = handler<GameTickEvent> {
        if (!isPlaying && autoPlay && loopMode == "列表循环" && musicList.isNotEmpty()) {
            playNext()
        }
        
        if (isPlaying && lyricTimestamps.isNotEmpty()) {
            val elapsed = System.currentTimeMillis() - playStartTime
            var newIndex = 0
            for (i in lyricTimestamps.indices) {
                if (elapsed >= lyricTimestamps[i]) {
                    newIndex = i
                } else {
                    break
                }
            }
            if (newIndex != currentLyricIndex && newIndex < lyricLines.size) {
                currentLyricIndex = newIndex
                currentLyric = lyricLines[currentLyricIndex]
            }
        }
        
        updateVolume()
    }

    fun scanMusicFiles() {
        musicList.clear()
        musicCache.clear()

        if (!musicDir.exists() || !musicDir.isDirectory) {
            updateMusicChoices()
            return
        }

        musicDir.walk()
            .filter { file ->
                file.isFile && (
                    file.extension.equals("mp3", true) ||
                    file.extension.equals("wav", true) ||
                    file.extension.equals("flac", true)
                )
            }
            .sortedBy { it.nameWithoutExtension.lowercase() }
            .forEach { file ->
                musicList.add(file)
                musicCache[file.name] = file.lastModified()
            }

        currentIndex = 0
        updateMusicChoices()
    }

    private fun updateMusicChoices() {
        val names = mutableListOf("无")
        names.addAll(musicListNames)
        
        musicChoicesValue.updateValues(names.toTypedArray())
        if (selectedMusicName !in names) {
            selectedMusicName = "无"
        }
    }

    fun playMusic(file: File) {
        stopMusic()

        if (!file.exists()) {
            chat("§c[音乐播放器] 文件不存在: ${file.name}")
            return
        }

        currentMusicFile = file
        selectedMusicName = file.nameWithoutExtension
        playStartTime = System.currentTimeMillis()
        
        loadLyrics(file)
        
        if (musicDuration <= 0) {
            musicDuration = getMusicDuration(file)
        }

        playThread = thread(start = true, name = "MusicPlayer-Thread") {
            try {
                isPlaying = true
                val inputStream = BufferedInputStream(FileInputStream(file))
                currentAudioDevice = VolumeControlledAudioDevice()
                currentAudioDevice?.setVolume(volumeValue / 100F)
                currentPlayer = Player(inputStream, currentAudioDevice)
                
                if (showInfo) {
                    chat("§a[音乐播放器] 正在播放: §f${file.nameWithoutExtension}")
                }

                currentPlayer?.play()

                if (isPlaying && currentPlayer?.isComplete == true) {
                    onMusicComplete()
                }
            } catch (e: Exception) {
                ClientUtils.LOGGER.error("[MusicPlayer] 播放失败: ${e.message}")
                chat("§c[音乐播放器] 播放失败: ${e.message}")
                isPlaying = false
            }
        }
    }

    private fun onMusicComplete() {
        when (loopMode) {
            "单曲循环" -> {
                currentMusicFile?.let { playMusic(it) }
            }
            "列表循环" -> {
                playNext()
            }
            "关闭" -> {
                isPlaying = false
            }
        }
    }

    fun stopMusic() {
        isPlaying = false
        try {
            currentPlayer?.close()
        } catch (e: Exception) {
        }
        currentPlayer = null
        currentAudioDevice = null
        playThread?.interrupt()
        playThread = null
        currentLyric = ""
        lyricLines = emptyList()
        lyricTimestamps.clear()
        playStartTime = 0
    }

    fun pauseMusic() {
        isPlaying = false
        if (showInfo) {
            chat("§e[音乐播放器] 已暂停")
        }
    }

    fun resumeMusic() {
        if (currentMusicFile != null && !isPlaying) {
            playMusic(currentMusicFile!!)
        }
    }

    fun playNext() {
        if (musicList.isEmpty()) return
        
        currentIndex = (currentIndex + 1) % musicList.size
        playMusic(musicList[currentIndex])
    }

    fun playPrevious() {
        if (musicList.isEmpty()) return
        
        currentIndex = (currentIndex - 1 + musicList.size) % musicList.size
        playMusic(musicList[currentIndex])
    }

    fun playByIndex(index: Int) {
        if (index in 0 until musicList.size) {
            currentIndex = index
            playMusic(musicList[index])
        }
    }

    private fun loadLyrics(musicFile: File) {
        currentLyric = ""
        lyricLines = emptyList()
        currentLyricIndex = 0
        lyricTimestamps.clear()
        musicDuration = 0L

        val lrcFile = File(musicFile.parentFile, musicFile.nameWithoutExtension + ".lrc")
        if (lrcFile.exists()) {
            try {
                val lines = mutableListOf<String>()
                val timestamps = mutableListOf<Long>()
                val timeRegex = Regex("\\[(\\d{2}):(\\d{2})\\.(\\d{2,3})\\]")
                
                lrcFile.readLines().forEach { line ->
                    if (line.isNotBlank() && line.contains("[")) {
                        val match = timeRegex.find(line)
                        if (match != null) {
                            val (minutes, seconds, millis) = match.destructured
                            val timeMs = minutes.toLong() * 60000 + seconds.toLong() * 1000 + 
                                if (millis.length == 2) millis.toLong() * 10 else millis.toLong()
                            val text = line.substring(match.range.last + 1).trim()
                            if (text.isNotEmpty()) {
                                timestamps.add(timeMs)
                                lines.add(text)
                            }
                        }
                    }
                }
                
                lyricLines = lines
                lyricTimestamps = timestamps
                
                if (lyricLines.isNotEmpty()) {
                    currentLyric = lyricLines[0]
                }
                
                if (lyricTimestamps.isNotEmpty()) {
                    val lastTimestamp = lyricTimestamps.last()
                    musicDuration = lastTimestamp + 10000L
                }
            } catch (e: Exception) {
                ClientUtils.LOGGER.warn("[MusicPlayer] 加载歌词失败: ${e.message}")
            }
        }
    }

    fun updateLyric(progressMs: Long) {
        if (lyricLines.isEmpty()) return
        
        val estimatedIndex = ((progressMs / 3000L).toInt()).coerceIn(0, lyricLines.size - 1)
        if (estimatedIndex != currentLyricIndex) {
            currentLyricIndex = estimatedIndex
            currentLyric = lyricLines[currentLyricIndex]
        }
    }

    private fun updateVolume() {
        currentAudioDevice?.setVolume(volumeValue / 100F)
    }

    fun setVolume(vol: Int) {
        volumeValue = vol.coerceIn(0, 100)
        updateVolume()
    }
    
    fun getVolume(): Int = volumeValue
    
    private class VolumeControlledAudioDevice : JavaSoundAudioDevice() {
        private var volumeControl: FloatControl? = null
        
        fun setVolume(volume: Float) {
            try {
                if (volumeControl == null) {
                    findVolumeControl()
                }
                volumeControl?.let { ctrl ->
                    val min = ctrl.minimum
                    val max = ctrl.maximum
                    val range = max - min
                    val gain = range * volume.coerceIn(0F, 1F) + min
                    ctrl.value = gain
                }
            } catch (e: Exception) {
            }
        }
        
        private fun findVolumeControl() {
            try {
                val field = JavaSoundAudioDevice::class.java.getDeclaredField("source")
                field.isAccessible = true
                val source = field.get(this) as? javax.sound.sampled.SourceDataLine
                if (source != null && source.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    volumeControl = source.getControl(FloatControl.Type.MASTER_GAIN) as FloatControl
                }
            } catch (e: Exception) {
            }
        }
    }
}
