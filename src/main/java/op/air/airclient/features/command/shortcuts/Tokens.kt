/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.features.command.shortcuts

open class Token

class Literal(val literal: String) : Token()

class StatementEnd : Token()
