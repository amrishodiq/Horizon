package com.durianberry.weather.view;

import com.durianberry.bbcommons.helper.ScaleUtilities;
import com.durianberry.weather.app.Config;
import com.durianberry.weather.app.WeatherApp;

import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.GIFEncodedImage;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.FullScreen;

public class SplashScreen extends FullScreen {
	public SplashScreen() {
		final EncodedImage logo = Config.getLogo();
		final GIFEncodedImage gif = (GIFEncodedImage) GIFEncodedImage.getEncodedImageResource("img/loader.gif");
		AnimatedGIFField gifField = new AnimatedGIFField(gif, FIELD_HCENTER | USE_ALL_WIDTH) {
			public void paintBackground(Graphics g) {
				g.setBackgroundColor(0x6699ff);
				g.clear();
			}
			public void layout(int w, int h) {
		    	setExtent(Display.getWidth(), super.getPreferredHeight());
		    }
		};
		
		BitmapField bitmap = new BitmapField() {
			public int getPreferredWidth() {
				return Display.getWidth();
			}
			public int getPreferredHeight() {
				return Display.getHeight() - gif.getHeight();
			}
			public void layout(int w, int h) {
				setExtent(getPreferredWidth(), getPreferredHeight());
			}
			public void paintBackground(Graphics g) {
				g.setBackgroundColor(0x6699ff);
				g.clear();
			}
			public void paint(Graphics g) {
				if (logo != null)
					g.drawImage(
						(getPreferredWidth() - logo.getScaledWidth())/2, 
						(getPreferredHeight() - logo.getScaledHeight())/2, 
						logo.getScaledWidth(), logo.getScaledHeight(), 
						logo, 0, 0, 0);
				else
					super.paint(g);
			}
		};
		add(bitmap);
		add(gifField);
	}
	
	public boolean keyDown(int keycode,
            int time) {		
		if (keycode == 1769472) { // escape pressed
			close();
			return true;
		}							
		return super.keyDown(keycode, time);		
	}
	
	private class AnimatedGIFField extends BitmapField 
	{
	    private GIFEncodedImage _image;     //The image to draw.
	    private int _currentFrame;          //The current frame in the animation sequence.
	    private int _width;                 //The width of the image (background frame).
	    private int _height;                //The height of the image (background frame).
	    private AnimatorThread _animatorThread;

	    public AnimatedGIFField(GIFEncodedImage image)
	    {
	        this(image, 0);
	    }

	    public AnimatedGIFField(GIFEncodedImage image, long style)
	    {
	        //Call super to setup the field with the specified style.
	        //The image is passed in as well for the field to
	        //configure its required size.
	        super(image.getBitmap(), style);

	        //Store the image and it's dimensions.
	        _image = image;
	        _width = image.getWidth();
	        _height = image.getHeight();
	        
	    }
	    
	    public void onDisplay() {
	    	//Start the animation thread.
	        _animatorThread = new AnimatorThread(this);
	        _animatorThread.start();
	        super.onDisplay();
	    }
	    
	    //Stop the animation thread when the screen the field is on is
	    //popped off of the display stack.
	    protected void onUndisplay()
	    {
	        _animatorThread.stop();
	        super.onUndisplay();
	    }

	    protected void paint(Graphics graphics)
	    {
	        //Call super.paint. This will draw the first background 
	        //frame and handle any required focus drawing.
	    	int pad = (getWidth() - _image.getWidth())/2;
	    	graphics.drawBitmap(pad, 0, _image.getWidth(), _image.getHeight(), _image.getBitmap(), 0, 0);

	        //Don't redraw the background if this is the first frame.
	        if (_currentFrame != 0)
	        {
	            //Draw the animation frame.
	            graphics.drawImage(pad + _image.getFrameLeft(_currentFrame), _image.getFrameTop(_currentFrame),
	                _image.getFrameWidth(_currentFrame), _image.getFrameHeight(_currentFrame), _image, _currentFrame, 0, 0);
	        }
	    }


	    //A thread to handle the animation.
	    private class AnimatorThread extends Thread
	    {
	        private AnimatedGIFField _theField;
	        private boolean _keepGoing = true; 
	        private int _totalFrames;     //The total number of frames in the image.
	        private int _loopCount;       //The number of times the animation has looped (completed).
	        private int _totalLoops;      //The number of times the animation should loop (set in the image).

	        public AnimatorThread(AnimatedGIFField theField)
	        {
	            _theField = theField;
	            _totalFrames = _image.getFrameCount();
	            _totalLoops = _image.getIterations();

	        }

	        public synchronized void stop()
	        {
	            _keepGoing = false;
	        }

	        public void run()
	        {
	            while(_keepGoing)
	            {
	                //Invalidate the field so that it is redrawn.
	                UiApplication.getUiApplication().invokeAndWait(new Runnable() 
	                {
	                    public void run() 
	                    {
	                        _theField.invalidate(); 
	                    }
	                }); 

	                try
	                {
	                    //Sleep for the current frame delay before
	                    //the next frame is drawn.
	                    sleep(_image.getFrameDelay(_currentFrame) * 10);
	                }
	                catch (InterruptedException iex)
	                {} //Couldn't sleep.

	                //Increment the frame.
	                ++_currentFrame; 

	                if (_currentFrame == _totalFrames)
	                {
	                    //Reset back to frame 0 if we have reached the end.
	                    _currentFrame = 0;

	                    ++_loopCount;

	                    //Check if the animation should continue.
	                    if (_loopCount == _totalLoops)
	                    {
	                        _keepGoing = false;
	                    }
	                }
	            }
	        }
	    }
	}
	
	private MenuItem about = new MenuItem("About", 1, 1) {
		public void run() {
			WeatherApp.get().showAbout();
		}
	};
	private MenuItem feature = new MenuItem("Request for Feature", 1, 1) {
		public void run() {
			WeatherApp.get().showFeatureRequest();
		}
	};
	private MenuItem other = new MenuItem("Other app by DurianApp", 1, 1) {
		public void run() {
			WeatherApp.get().showOtherApp();
		}
	};
	private MenuItem facebook = new MenuItem("Share to Facebook", 1, 1) {
		public void run() {
			WeatherApp.get().shareToFacebook();
		}
	};
	private MenuItem twitter = new MenuItem("Share to Twitter", 1, 1) {
		public void run() {
			WeatherApp.get().shareToTwitter();
		}
	};
	private MenuItem email = new MenuItem("Share by Email", 1, 1) {
		public void run() {
			WeatherApp.get().shareByEmail();
		}
	};
	protected void makeMenu(Menu menu, int instance) {
		menu.add(about);
		menu.addSeparator();
		menu.add(feature);
		menu.add(other);
		menu.addSeparator();
		menu.add(facebook);
		menu.add(twitter);
		menu.add(email);
	}
}
