import urllib
import urllib2
from os.path import basename
from urlparse import urlsplit
from google import search
from string import replace
from BeautifulSoup import BeautifulSoup
import re
import unicodedata
import time
import sys

links = open("links.html", "a")
failed = open("failed.html", "a")

def slugify(value):
    """
    Normalizes string, converts to lowercase, removes non-alpha characters,
    and converts spaces to hyphens.
    """
    value = unicodedata.normalize('NFKD', value).encode('ascii', 'ignore')
    value = unicode(re.sub('[^\w\s-]', '', value).strip().lower())
    value = re.sub('[-\s]+', '-', value)
    return value

def download(url, title, i=0):
    if (i == 3): # Try to download 3 times, then move on
        failed.write('<li><a href="'+url+'" title="'+title+'">'+title+'</a>\n')
        print('Failed - '+title)
        return
    try:
        localFileName = slugify(title)+'.pdf'
        urllib.urlretrieve(url, localFileName)
        links.write('<li><a href="'+localFileName+'" title="'+title+'">'+title+'</a>\n')
        print("Complete - "+localFileName)
        return
    except:
        print(sys.exc_info()[1])
        print("Error, sleeping for 1 second")
        time.sleep(1)
        download(url, localFileName, i+1)
        return
def google(string):
    for url in search(string):
        return url
def getPaper(title):
    link = None
    try:
        link = google('"'+title+'" filetype:pdf') #  Gets exact first result of type pdf
    except:
        time.sleep(42)
        try:
            link = google('"'+title+'" filetype:pdf') #  Gets exact first result of type pdf
        except:
            pass
    if(link is not None):
        print("Found - "+title)
        download(link, title)
    else:
        print('NOTFOUND: '+title)
        failed.write('<li>'+title+' (Not found)\n')
    return
def getHTML(url):
    req = urllib2.Request(url)
    response = urllib2.urlopen(req)
    return response.read()
def doForURL(baseurl):
    print("Starting up...")
    
    soup = BeautifulSoup(getHTML(baseurl))
    
    oqs = soup.find(attrs={'id' : 'oqs'})
    url_string = 'http://ieeexplore.ieee.org/xpl/tocresult.jsp?' + oqs['value'] + '&pageNumber=1' + '&rowsPerPage=9000'
    print('Found URL - '+url_string)
    soup = BeautifulSoup(getHTML(url_string))

    for detailNode in soup.findAll(attrs={'class' : 'detail'}) :
        title = detailNode.find('h3').find('a')
        if(title is not None):
            getPaper(""+title.string.strip(' \t\n\r'))


doForURL('http://ieeexplore.ieee.org/xpl/mostRecentIssue.jsp?punumber=7771')

links.close()
failed.close()
