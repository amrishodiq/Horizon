/**
 * Copyright © 1998-2010 Research In Motion Ltd.
 *
 * Note:
 *
 * 1. For the sake of simplicity, this sample application may not leverage
 * resource bundles and resource strings.  However, it is STRONGLY recommended
 * that application developers make use of the localization features available
 * within the BlackBerry development platform to ensure a seamless application
 * experience across a variety of languages and geographies.  For more information
 * on localizing your application, please refer to the BlackBerry Java Development
 * Environment Development Guide associated with this release.
 *
 * 2. The sample serves as a demonstration of principles and is not intended for a
 * full featured application. It makes no guarantees for completeness and is left to
 * the user to use it as sample ONLY.
 */

package com.durianberry.weather.view;

import java.util.Random;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

/**
 * 
 *
 * @author rhaq
 * @version 1.00 Aug 24, 2010 Initial submission.
 * @since TabManagerDemo 
 *
 */
public class TabManagerScreen extends MainScreen implements ListFieldCallback
{
    private Bitmap _bitmap1 = Bitmap.getBitmapResource("icon1.png");
    private Bitmap _bitmap2 = Bitmap.getBitmapResource("icon2.png");
    private Bitmap _bitmap3 = Bitmap.getBitmapResource("icon3.png");
    private Bitmap _bitmapArr[] = new Bitmap[]{_bitmap1, _bitmap2, _bitmap3};
    private final Random generator = new Random();
    
    private ListField _listField;

    private String text = "This is a list row";


    /**
     * 
     */
    public TabManagerScreen()
    {
        super(NO_VERTICAL_SCROLL);

        String[] tabs = new String[] {"Text", "Images", "List Fields"};

        VerticalFieldManager texts = addTexts();
        VerticalFieldManager images = addImages();
        VerticalFieldManager mixed = new VerticalFieldManager(VERTICAL_SCROLL);

        _listField = new ListField(20);
        _listField.setRowHeight(64);
        _listField.setCallback(this);
        mixed.add(_listField);


        Field[] fields = new Field[]{ texts, images, mixed };

        add( new TabManager(tabs, fields) );
    }

    public void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) 
    {
    	Bitmap bitmap = _bitmapArr[0];
        graphics.drawBitmap(0, y + (64 - bitmap.getHeight()) / 2, bitmap.getWidth(), bitmap.getHeight(), bitmap, 0, 0);
        graphics.drawText(text + " " + index, bitmap.getWidth() + 4, y, DrawStyle.ELLIPSIS, width);
    }
    public Object get(ListField arg0, int index) {
        return null;
    }
    public int getPreferredWidth(ListField listField) {
        return 0;
    }
    public int indexOfList(ListField arg0, String arg1, int arg2) {
        return 0;
    }
    /**
     * @return
     */
    private VerticalFieldManager addImages()
    {
        VerticalFieldManager imagesInVfm = new VerticalFieldManager(VERTICAL_SCROLL);

        HorizontalFieldManager hfm = null;
        
        for(int i=0; i < 10; i++)
        {
        	 hfm = new HorizontalFieldManager();
        	 hfm.add( new BitmapField(_bitmapArr[generator.nextInt(_bitmapArr.length)]));
             hfm.add( new RichTextField("Bitmap image and RichTextField inside a HorizontalFieldManager as row #" + i) );
             imagesInVfm.add(hfm);
             if( i < 9 )
            	 imagesInVfm.add( new SeparatorField() );
        }
        return imagesInVfm;
    }
    /**
     * @return
     */
    private VerticalFieldManager addTexts()
    {
        VerticalFieldManager texts = new VerticalFieldManager(VERTICAL_SCROLL);
        for (int i = 0; i < 40; i++)
        {
            texts.add( new RichTextField("This is RichTextField "+(i+1)) );
            if ( i < 39 )
                texts.add( new SeparatorField() );
        }
        return texts;
    }

}