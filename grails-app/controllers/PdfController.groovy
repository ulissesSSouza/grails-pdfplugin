
class PdfController {

    PdfService pdfService

    def index = {
        redirect(action: demo)
    }

    def pdfLink = {
        def baseUri = request.scheme + "://" + request.serverName + ":" + request.serverPort +
                    grailsAttributes.getApplicationUri(request)
        println "BaseUri is $baseUri"
		
        def url = baseUri + params.url
        println "Fetching url $url"

        try{
        	byte[] b = pdfService.buildPdf(url)
	        response.setContentType("application/pdf")
	        response.setHeader("Content-disposition", "attachment; filename=" + (params.filename ?: "document.pdf"))
	        response.setContentLength(b.length)
	        response.getOutputStream().write(b)
	    }
		catch (Throwable e) {
            println "there was a problem with PDF generation ${e}"
			redirect(uri:params.url + '?' + request.getQueryString())
		}
    }
    
    def pdfForm = {
        try{
            byte[] b
            if(request.method == "GET") {            
                def baseUri = request.scheme + "://" + request.serverName + ":" + request.serverPort +
                              grailsAttributes.getApplicationUri(request)
                def url = baseUri + params.url + '?' + request.getQueryString()
                println "BaseUri is $baseUri"
                println "Fetching url $url"
            	b = pdfService.buildPdf(url)
            }
            if(request.method == "POST"){
                def content
                if(params.template){
                    println "Template: $params.template"
                    content = g.render(template:params.template, model:[pdf:params])
                }
                else{
                    println "GSP - Controller: $params.pdfController , Action: $params.pdfAction"
                    content = g.include(controller:params.controller, action:params.action)
                }
            	b = pdfService.buildPdfFromString(content)
            }
    		response.setContentType("application/pdf")
            response.setHeader("Content-disposition", "attachment; filename=" + (params.filename ?: "document.pdf"))
            response.setContentLength(b.length)
            response.getOutputStream().write(b)
        }
		catch (Throwable e) {
            println "there was a problem with PDF generation ${e}"
			redirect(uri:params.url + '?' + request.getQueryString())
		}
    }
    
    def demo = {
    	def firstName = params.first ?: "Eric"
    	def lastName = params.last ?: "Cartman"
    	def age = params.age
    	return [firstName:firstName, lastName:lastName, age:age]
    }
    
    def demo2 = {
    	def id = params.id
    	def name = params.name
    	def age = params.age
    	def randomString = params.randomString ?: "PDF creation is a blast!!!"
    	def food = params.food
    	def hometown = params.hometown
    	return [id:id, name:name, age:age, randomString:randomString, food:food, hometown:hometown]
    }
}
