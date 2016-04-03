package module6;

//import processing.core.PGraphics;
import processing.core.PShape;
/** Author: UC San Diego Intermediate Software Development MOOC team
 * @student Dmitry Sergeev, Minsk, Belarus.
 * Date: March 17, 2016
 * */
public final class PShapeWrapper implements PWrapper {
	
	private PShape ps;
	//private PGraphics pg;
	
	public PShapeWrapper(PShape ps){
		this.ps = ps;
	}

	@Override
	public void drawMarker() {
		((PWrapper) ps).drawMarker();
		
	}
	


	public PShape getPs() {
		return ps;
	}

	public void setPs(PShape ps) {
		this.ps = ps;
	}

}
