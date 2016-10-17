
import org.jsoup._
import fs2._

val wiki = "https://en.wikipedia.org/wiki/List_of_companies_of_New_Zealand"
url("")

def url(s: String): Nothing

Jsoup.connect(wiki).get().select("a")



