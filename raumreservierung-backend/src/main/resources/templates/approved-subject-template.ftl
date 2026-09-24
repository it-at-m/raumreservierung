<#if environment?? && environment?has_content && environment?upper_case != "PROD">
[${environment?upper_case}] <#rt>
</#if>
Es gibt Neuigkeiten zu Ihrer Buchung: "${booking.title}"