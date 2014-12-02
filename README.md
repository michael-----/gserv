#    gServ

##Groovy API for <u><b>Container Free </b></u>RESTful Services.

####Java 1.6+ is required.

<table>
<tr><th colspan='2'>
Creating REST Resources
</th></tr>
<tr><td width='60%'>
<pre>

/// create a GServ instance
def gserv = new GServ()

/// Create a Books REST resource
def bkResource = gserv.resource("/books") {
    // URI:  /books/faq
    get(“/faq”, file(“BooksFaq.html”))
    
    // URI: /books/xyz
    get(“:id)”, { id ->
        def book = bookService.get( id )
        writeJson(book)
    }
    
    // responds  to /books/all
    get(“/all”, {  ->
        def books = bookService.allBooks ()
        header(“content-type”, “application/json”)
        writeJSON(books)
    })
}

</pre>
</td>
<td width='40%'>
The root path is passed to the GServ.resource() method along with a closure defining the routing for the resource.
</td>
</tr>
<tr>
<th colspan='2'>
Creating a Server Instance
</th>
</tr>
<tr>
<td>
<pre>

gserv.http {
    // setup a directory for static files
    static_root  '/public/webapp'

    //static FAQ page located at '/public/webapp/App.faq.html'
    get('/faq', file("App.faq.html"))
    
}.start(8080);


</pre>
</td>
<td>
The http() method creates a GServInstance that can later listen on a port and handle HTTP requests. This server instance
defines static roots usually used for templates for single-page apps and a single FAQ page.
Then, after the server instance is returned from the http() method, we can immediately call start(8080) on it.
</td>
</tr>
<tr><th colspan='2'>
Adding Resources to a Server Instance
</th>
</tr><tr>
<td>
<pre>

def bkResource = gserv.resource("/books") { ... }
def userResource = gserv.resource("/users") { ... }

gserv.http {
    // setup a directory for static files
    static_root ("/public/webapp")

    // static FAQ page located at '/public/webapp/App.faq.html'
    get('/faq', file('App.faq.html'))

    /// add Book and User REST resources to our GServ instance
    resource(bkResource)
    resource(userResource)
 }.start(8080);

</pre>
</td>
<td>
A server instance can be created by simply adding resources.  Here we add our 2 resources: bkResources and
userResources.  Now, all URIs related to both resources are available once the instance is started. This instance also
defines a static_root which tells gserv where to find static files such as the FAQ page which should be at /public/webapp/App.faq.html.
</td>
</tr>
</table>