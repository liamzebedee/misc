/*
 * Created on 20.11.2007
 * emonic.base org.emonic.base.infostructure BracketInfo.java
 */
package org.emonic.base.infostructure;

import java.util.ArrayList;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

/**
 * This class locates the brackets of a document.
 * @author bb
 *
 */
public class BracketInfo {

	
	static char rbrack='(';
	static char rlbrack = ')';
	static char cbrack='{';
	static char clbrack = '}';
	static char dbrack='[';
	static char dlbrack = ']';
	static char ebrack='<';	
	static char elbrack = '>';
	private BracketKind[] rkindA;
	private BracketKind[] ckindA;
	private BracketKind[] dkindA;
	private BracketKind[] ekindA;
	
	public BracketInfo(IDocument doc){
		parseDoc(doc);
	}
	
	
	private void parseDoc(IDocument doc) {
		boolean inLineCom=false;
		boolean inBlockCom=false;
		int rcount = 0;
		int ccount = 0;
		int dcount = 0;
		int ecount = 0;
		ArrayList rbracks = new ArrayList();
		ArrayList cbracks = new ArrayList();
		ArrayList dbracks = new ArrayList();
		ArrayList ebracks = new ArrayList();
		
		for (int i = 0; i < doc.getLength(); i++){
		    try {
				char actCar = doc.getChar(i);
//				 Are we at the beginning of a comment?
				if ((actCar=='/')&&(i < doc.getLength()-1)){
					char nextCar = doc.getChar(i+1);
					if (nextCar=='/'){
						inLineCom=true;
					} else if (nextCar=='*'){
						inBlockCom=true;
					}
				} else if ((actCar=='*')&&(i < doc.getLength()-1)){
				     // End of a block comment?
					char nextCar = doc.getChar(i+1);
					if (nextCar=='/'){
						inBlockCom=false;
					} 
				} else if ((actCar=='\n')){
					inLineCom=false;
				} else if ((! inBlockCom) && ! inLineCom){
					// Do the bracket parsing
					// for ()
					if (actCar==rbrack){
						// its a (
						BracketKind rbrKind = new BracketKind();
						rbrKind.setSign(actCar);
						rbrKind.setOpening(i);
						rbrKind.setCurlcount(rcount);
						rcount++;
						rbracks.add(rbrKind);
					} else if (actCar==rlbrack){
					  // its a ), find the opening and complete
						rcount --;
						boolean found = false;
						for (int j =0; (j < rbracks.size() && ! found);j++){
							BracketKind act = (BracketKind) rbracks.get(j);
							if (act.getSign()==rbrack && act.getCurlcount() == rcount && act.getClosing()==0){
								found = true;
								act.setClosing(i);
							}
						}
					
					}
                    // for {}
					else  if (actCar==cbrack){
						// its a {
						BracketKind cbrKind = new BracketKind();
						cbrKind.setSign(actCar);
						cbrKind.setOpening(i);
						cbrKind.setCurlcount(ccount);
						ccount++;
						cbracks.add(cbrKind);
					} else if (actCar==clbrack){
					  // its a }, find the opening and complete
						ccount --;
						boolean found = false;
						for (int j =0; (j < cbracks.size() && ! found);j++){
							BracketKind act = (BracketKind) cbracks.get(j);
							if (act.getSign()==cbrack && act.getCurlcount() == ccount && act.getClosing()==0 ){
								found = true;
								act.setClosing(i);
							}
						}
					
					}
                    // for []
					else  if (actCar==dbrack){
						// its a {
						BracketKind dbrKind = new BracketKind();
						dbrKind.setSign(actCar);
						dbrKind.setOpening(i);
						dbrKind.setCurlcount(dcount);
						dcount++;
						dbracks.add(dbrKind);
					} else if (actCar==dlbrack){
					  // its a }, find the opening and complete
						dcount --;
						boolean found = false;
						for (int j =0; (j < dbracks.size() && ! found);j++){
							BracketKind act = (BracketKind) dbracks.get(j);
							if (act.getSign()==dbrack && act.getCurlcount() == dcount && act.getClosing()==0){
								found = true;
								act.setClosing(i);
							}
						}
					
					}
                    //	for <>
					else  if (actCar==ebrack){
						// its a {
						BracketKind ebrKind = new BracketKind();
						ebrKind.setSign(actCar);
						ebrKind.setOpening(i);
						ebrKind.setCurlcount(ecount);
						ecount++;
						ebracks.add(ebrKind);
					} else if (actCar==elbrack){
					  // its a }, find the opening and complete
						ecount --;
						boolean found = false;
						for (int j =0; (j < ebracks.size() && ! found);j++){
							BracketKind act = (BracketKind) ebracks.get(j);
							if (act.getSign()==ebrack && act.getCurlcount() == ecount && act.getClosing()==0){
								found = true;
								act.setClosing(i);
							}
						}
					
					}
					
				}
				
				
			} catch (BadLocationException e) {
				// Should never happen
				e.printStackTrace();
			}	
		}
//		 Convert the arraylists in arrays
		rkindA = new BracketKind[rbracks.size()];
		rkindA=(BracketKind[]) rbracks.toArray(rkindA);
		ckindA = new BracketKind[cbracks.size()];
		ckindA=(BracketKind[]) cbracks.toArray(ckindA);
		dkindA = new BracketKind[dbracks.size()];
		dkindA=(BracketKind[]) dbracks.toArray(dkindA);
		ekindA = new BracketKind[ebracks.size()];
		ekindA=(BracketKind[]) ebracks.toArray(ekindA);
		
	}

	/**
	 * Return a corresponding bracket, return startpos if none is found
	 * @param bracket
	 * @param startpos
	 * @return
	 */
	public int getCorresponding(char bracket, int startpos) {
		if (bracket == rbrack ){
			for (int i = 0; i < rkindA.length; i++){
				if (rkindA[i].getOpening()==startpos) return rkindA[i].getClosing();
			}
		}
		if (bracket == rlbrack ){
			for (int i = 0; i < rkindA.length; i++){
				if (rkindA[i].getClosing()==startpos) return rkindA[i].getOpening();
			}
		}
		
		if (bracket == cbrack ){
			for (int i = 0; i < ckindA.length; i++){
				if (ckindA[i].getOpening()==startpos) return ckindA[i].getClosing();
			}
		}
		if (bracket == clbrack ){
			for (int i = 0; i < ckindA.length; i++){
				if (ckindA[i].getClosing()==startpos) return ckindA[i].getOpening();
			}
		}
		
		if (bracket == dbrack ){
			for (int i = 0; i < dkindA.length; i++){
				if (dkindA[i].getOpening()==startpos) return dkindA[i].getClosing();
			}
		}
		if (bracket == dlbrack ){
			for (int i = 0; i < dkindA.length; i++){
				if (dkindA[i].getClosing()==startpos) return dkindA[i].getOpening();
			}
		}
		
		if (bracket == ebrack ){
			for (int i = 0; i < ekindA.length; i++){
				if (ekindA[i].getOpening()==startpos) return ekindA[i].getClosing();
			}
		}
		if (bracket == elbrack ){
			for (int i = 0; i < ekindA.length; i++){
				if (ekindA[i].getClosing()==startpos) return ekindA[i].getOpening();
			}
		}
		return startpos;
	}
	

	class BracketKind{
		char sign;
		int opening =0;
		int closing=0;
		int curlcount=-1;
		/**
		 * @return the closing
		 */
		public int getClosing() {
			return closing;
		}
		/**
		 * @param closing the closing to set
		 */
		public void setClosing(int closing) {
			this.closing = closing;
		}
		/**
		 * @return the opening
		 */
		public int getOpening() {
			return opening;
		}
		/**
		 * @param opening the opening to set
		 */
		public void setOpening(int opening) {
			this.opening = opening;
		}
		/**
		 * @return the sign
		 */
		public char getSign() {
			return sign;
		}
		/**
		 * @param sign the sign to set
		 */
		public void setSign(char sign) {
			this.sign = sign;
		}
		/**
		 * @return the curlcount
		 */
		public int getCurlcount() {
			return curlcount;
		}
		/**
		 * @param curlcount the curlcount to set
		 */
		public void setCurlcount(int curlcount) {
			this.curlcount = curlcount;
		}
	}


	
}
