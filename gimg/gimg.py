"""
googleImageLoader ===============================
  by liamzebedee <liamzebedee@yahoo.com.au>
  Licensed under GPLv3 to Liam Edwards-Playne
=================================================

This plugin provides a simple interface to load
images from Google relevant to the card front. 
Requires pip package 'beautifulsoup4'. 
"""

import types
from aqt import *
from aqt.qt import *
from aqt.utils import shortcut, showInfo
from aqt.addcards import AddCards
from anki.hooks import wrap
from bs4 import BeautifulSoup
import urllib
import urllib2
import tempfile
import sys
from PyQt4.QtGui import *
from PyQt4.QtCore import *

def getTextFromHTML(html):
	if html is None: return
	return BeautifulSoup(html).get_text()

class ClickableImage(QLabel):
	def __init__(self, imgFile, parent = None):
		QLabel.__init__(self, parent)
		self.setPixmap(QPixmap(imgFile.name))
		self.imgFile = imgFile

	def mouseReleaseEvent(self, event):
		self.emit(SIGNAL('clicked'), self)
		
	def close(self):
		self.imgFile.close()

def addLoadImageBtn(self):
	self.imgLoaderBtn = self.form.buttonBox.addButton(_("Load Image"), QDialogButtonBox.ActionRole)
	self.imgLoaderBtn.setShortcut(QKeySequence("Ctrl+O"))
	self.imgLoaderBtn.clicked.connect(openImageLoaderDialog)
	self.imgLoaderBtn.setToolTip(shortcut(_("Find an suitable image using Google and insert it (shortcut: Control+O)")))
AddCards.setupButtons = wrap(AddCards.setupButtons, addLoadImageBtn)

class imageLoaderDialog(QDialog):
	def __init__(self, parent=None):
		super(imageLoaderDialog, self).__init__(parent)
		self.parent = parent
        
        # Setup dialog
		self.setWindowModality(Qt.WindowModal)
		self.resize(250, 400)
		
		# Setup layout
		self.layout = QVBoxLayout(self)
		scroll = QScrollArea()
		scroll.setWidgetResizable(True)
		self.layout.addWidget(scroll)
	
		guideLbl = QLabel("Click an image to use it.")
		self.layout.addWidget(guideLbl)
	
		cancelBtn = QPushButton("Cancel/Close")
		self.layout.addWidget(cancelBtn)
		cancelBtn.clicked.connect(self.close)
		
		# Setup scroll area
		scrollContents = QWidget()
		scroll.setWidget(scrollContents)
		self.layout = QVBoxLayout(scrollContents)
		
	def load_images(self):
		front = self.parent.editor.note['Front']
		
		def imgCallback(imgFile):
			imgWidget = ClickableImage(imgFile)
			#self.destroyed.connect(imgWidget.close) XXX: not working
			#mw.connect(self, SIGNAL("destroyed"), imgWidget.close)
			mw.connect(imgWidget, SIGNAL("clicked"), self.selectImage)
			self.layout.addWidget(imgWidget)
			imgWidget.show()
	
		# Load images
		google_images(getTextFromHTML(front), imgCallback)
	
	def selectImage(self, label):
		title = getTextFromHTML(self.parent.editor.note['Front'])
		self.parent.editor.note['Front'] = ""
		self.parent.editor.loadNote()
		
		# Add image
		self.parent.editor.addMedia(label.imgFile.name, canDelete=True)
		
		# Get image and apply title/alt
		soup = BeautifulSoup(self.parent.editor.note['Front'])
		img = soup.find('img')
		img.attrs['title'] = title
		img.append(BeautifulSoup('<div style="display:none">'+title+'</div>')) # Hidden text that allows us to search the title
		self.parent.editor.note['Front'] = str(soup)
		self.parent.editor.loadNote()
		
		self.close()
	
	# TODO more idiomatic cleanup with signals etc.	
	def closeEvent(self, event):
		for i in xrange(self.layout.count()):
			item = self.layout.itemAt(i)
			widget = item.widget()       
			if widget:
				try:
					widget.close()
				except:
					pass
		

def openImageLoaderDialog():
	parentWindow = aqt.mw.app.activeWindow() or aqt.mw # editor window	
	front = parentWindow.editor.note['Front']
	if front == "":
		return showInfo("Front of card shouldn't be empty at this point. Go back and put something there!")
	
	dialog = imageLoaderDialog(parentWindow)
	dialog.setModal(True)
	dialog.show()
	# TODO load images after window shows
	dialog.load_images()

def google_images(query, callback):
	opener = urllib2.build_opener()
	opener.addheaders = [('Accept Language', 'en-GB,en-US;q=0.8,en;q=0.6'), ('User-agent', 'Mozilla/5.0')]
	# NOTE: Google returns a different table-based page structure when using urllib with the above user agent.
	#		Keep this in mind if editing the scraper. 
	page = opener.open(urllib.quote("http://www.google.com/search?tbm=isch&q="+query, safe="%/:=&?~#+!$,;'@()*[]"))
	
	soup = BeautifulSoup(page.read())
	g_image_table = soup.find("table", { "class": "images_table" } )
	images = g_image_table.findAll("img", limit = 15)
	
	for image in images:
		imgThumbnailData = urllib2.urlopen(image['src']).read()
		
		imgFile = tempfile.NamedTemporaryFile(suffix=".jpg")
		imgFile.seek(0)
		imgFile.write(imgThumbnailData)
		imgFile.flush()
		
		callback(imgFile)
