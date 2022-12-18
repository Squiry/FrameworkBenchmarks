package ru.tinkoff.kora.techempower.kotlin

import gg.jte.TemplateOutput
import gg.jte.generated.precompiled.JtefortunesGenerated
import gg.jte.html.HtmlTemplateOutput
import gg.jte.html.OwaspHtmlTemplateOutput
import ru.tinkoff.kora.common.Component
import ru.tinkoff.kora.jte.common.JteTemplateWriter

data class Fortune(val id: Int, val message: String)


data class FortunesTemplate(val fortunes: List<Fortune>)


@Component
class FortunesTemplateJteWriter : JteTemplateWriter<FortunesTemplate> {
    override fun write(value: FortunesTemplate, output: TemplateOutput) {
        val realOutput = output as? HtmlTemplateOutput ?: OwaspHtmlTemplateOutput(output)
        JtefortunesGenerated.render(realOutput, null, value.fortunes.map { ru.tinkoff.kora.techempower.common.Fortune(it.id, it.message) })
    }
}
