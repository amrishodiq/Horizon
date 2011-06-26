//#preprocess
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

import java.util.Vector;

import com.durianberry.bbcommons.transitions.interpolator.InterpolatedValue;
import com.durianberry.bbcommons.ui.component.AbsoluteFieldManager;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.MathUtilities;


/**
 * <code>TabManager</code> allows "content" fields to be switched by
 * an animated tab carousel field.
 * <p/>
 * Sample Use:
 * <p/>
 * <code>
 * new TabManager(new String[] { "1", "2", "3" }, new LabelField[] { new LabelField("label 1"), new LabelField("label 2"), new LabelField("label 3" ) } )
 * </code>
 *
 * @author iquick@rim.com
 */
public class TabManager extends VerticalFieldManager {

    /** Defaults. */
    private static final int ALPHA_FADE = 84;

    /** The Constant DEFAULT_UNSELECTED_TEXT_COLOUR. */
    private static final int DEFAULT_UNSELECTED_TEXT_COLOUR = 0xBBBBBB;

    /** The Constant FADE_WIDTH. */
    private static final int FADE_WIDTH = 40;

    /** The Constant HORIZ. */
    private static final int HORIZ = 0;

    /** The Constant NO_DIR. */
    private static final int NO_DIR = -1;

    /** The Constant SELECTED_TEXT_COLOUR. */
    private static final int SELECTED_TEXT_COLOUR = Color.WHITE;

    /** The Constant SEPARATOR_ALPHA. */
    private static final int SEPARATOR_ALPHA = 225;

    /** The Constant SEPARATOR_COLOR. */
    private static final int SEPARATOR_COLOR = Color.DIMGRAY;

    /** The Constant VERT. */
    private static final int VERT = 1;

    /** The _content manager. */
    protected TabContentManager _contentManager;

    /** The _current index. */
    protected int _currentIndex;

    /** The tabbed contents. */
    protected Vector _tabContents;

    /** The original tab titles. */
    protected Vector _tabTitles;

    /** The _x interpolator. */
    protected InterpolatedValue _xInterpolator;

    /** The _background. */
    private Bitmap _background;

    /** The _direction. */
    private int _direction;

    /** The _event fired. */
    private boolean _eventFired;

    /** The _fade. */
    private Bitmap _fade;

    /** header bgs. */
    private Bitmap _focusedBackground;

    /** The _focused fade. */
    private Bitmap _focusedFade;

    /** tiles for expanding into the header bg. */
    private Bitmap _focusedTile;

    /** The _invalidate runnable. */
    private InvalidateRunnable _invalidateRunnable;

    /** The _label header height. */
    private int _labelHeaderHeight;

    /** The _label height. */
    private int _labelHeight;

    /** header label dimensions. */
    private int _labelWidth;

    /** The listener. */
    private FieldChangeListener _listener;

    /** The _previous touch x. */
    private int _previousTouchX;

    /** The _previous touch y. */
    private int _previousTouchY;

    /** The _scroll dir. */
    private int _scrollDir;

    /** text colours. */
    private int _textColour;

    /** The _unfocused tile. */
    private Bitmap _unfocusedTile;

    /** The _unselected text color. */
    private int _unselectedTextColor;

    /** tab titles wrapped to _labelWidth. */
    private String[][] _wrappedTitles;

    /** The last key press. */
    private long lastKeyPress;

    /**
     * Default font is set in the constructor, to a bolded one of the default.
     * @param tabTitles the header tab titles
     * @param tabContents the contents of the tabs
     */
    public TabManager(String[] tabTitles, Field[] tabContents) {
        this(tabTitles, tabContents, 0L);
    }

    /**
     * same as the other constructor but with a flag to pass to the Manager superclass.
     * @param tabTitles the tab titles
     * @param tabContents the tab contents
     * @param flags the flags
     */
    public TabManager(String[] tabTitles, Field[] tabContents, long flags) {
        super(flags);

        if (tabContents.length != tabTitles.length)
            throw new IllegalArgumentException("Input arrays must be of equal length");//$NON-NLS-1$

        _contentManager = new TabContentManager();
        _tabContents = new Vector();
        _tabTitles = new Vector();
        _unselectedTextColor = DEFAULT_UNSELECTED_TEXT_COLOUR;
        _textColour = SELECTED_TEXT_COLOUR;
        _scrollDir = -1;

        super.add(new TabHeader());
        super.add(_contentManager);

        this.setCurrentIndex(0);
        for (int i = 0; i < tabTitles.length; ++i) {
            _tabTitles.addElement(tabTitles[i]);
        }

        this.setFont(this.getFont().derive(Font.BOLD));
        setContents(tabContents);

        _xInterpolator = new InterpolatedValue();
    }

    public void paintBackground(Graphics g) {
    	g.setBackgroundColor(0x6699ff);
    	g.clear();
    }
    
    /**
     * Adds the tab.
     * @param label the label
     * @param f the f
     */
    public void addTab(String label, Field f) {
        _tabTitles.addElement(label);
        _tabContents.addElement(f);

        _contentManager.deleteAll();
        for (int i = 0; i < _tabContents.size(); i++) {
            _contentManager.add((Field) _tabContents.elementAt(i));
        }
        this.calculateLabelDimensions();
    }


    /**
     * Gets the content field.
     * @param i field index
     * @return the field
     */
    public Field getContentField(int i) {
        return (Field) _tabContents.elementAt(i);
    }

    /**
     * gets the current selection index. This may not correspond with what you see at the center during animations.
     * @return the current index
     */
    public int getCurrentIndex() {
        int r = _xInterpolator.getCurrentValue() / _labelWidth;

        return r;
    }

    /**
     * Gets the label height.
     * @return the height of the header labels
     */
    public int getLabelHeight() {
        return _labelHeight;
    }

    /**
     * Gets the label width.
     * @return the width of the header labels
     */
    public int getLabelWidth() {
        return _labelWidth;
    }

    /**
     * gets the final selection index. This may not correspond with what you see at the center during animations.
     * @return the target index
     */
    public int getTargetIndex() {
        int r = (_xInterpolator.getFinalValue() / _labelWidth);

        return r;
    }

    /**
     * Gets the unselected text colour.
     * @return the unselected text colour
     */
    public int getUnselectedTextColour() {
        return _unselectedTextColor;
    }

    /**
     * Handle swipe.
     * @param dx Magnitude of navigational motion: negative for a move left and postive for a move right.
     * @param time the time
     * @return whether base action (navigation movement or touch events) should be consumed
     */
    public boolean handleSwipe(int dx, int time) {
        long dt = (time - lastKeyPress);
        if (dt < 200) {
            return true;
        }
        _xInterpolator.kick(((dx > 0) ? _labelWidth : -_labelWidth));
        animate();
        if (!this.getField(0).isFocus()) {
            _contentManager.getField(getTargetIndex() + 1).setFocus();
        }
        lastKeyPress = time;
        return true;
    }

    /**
     * Insert tab.
     * @param label the label
     * @param f the f
     * @param index the index
     */
    public void insertTab(String label, Field f, int index) {
        _tabTitles.insertElementAt(label, index);
        _tabContents.insertElementAt(f, index);


        _contentManager.deleteAll();
        for (int i = 0; i < _tabContents.size(); i++) {
            _contentManager.add((Field) _tabContents.elementAt(i));
        }

        this.calculateLabelDimensions();
    }

    /**
     * we are focusable.
     * @return true, if is focusable
     */
    public boolean isFocusable() {
        return true;
    }

    /**
     * Removes the tab.
     * @param index the index
     */
    public void removeTab(int index) {
        Field f = (Field) _tabContents.elementAt(index);

        _tabTitles.removeElementAt(index);
        _tabContents.removeElementAt(index);

        _contentManager.delete(f);

        this.calculateLabelDimensions();
    }

    /**
     * Sets the contents.
     * @param f the new field contents
     */
    public void setContents(Field[] f) {
        if (f.length != _tabTitles.size()) {
            throw new IllegalArgumentException();
        }

        _tabContents.setSize(0);
        for (int i = 0; i < f.length; ++i) {
            _tabContents.addElement(f[i]);
        }

        _contentManager.deleteAll();

        _contentManager.add(new SpacerField());
        for (int i = 0; i < _tabContents.size(); i++) {
            _contentManager.add((Field) _tabContents.elementAt(i));
        }
    }

    /**
     * Sets the contents.
     * @param tabTitles the tab titles
     * @param f the tab content fields
     */
    public void setContents(String[] tabTitles, Field[] f) {
        if (tabTitles.length != f.length)
            throw new IllegalArgumentException();

        _tabTitles.setSize(0);
        for (int i = 0; i < tabTitles.length; ++i) {
            _tabTitles.addElement(tabTitles[i]);
        }
        this.setContents(f);
        this.calculateLabelDimensions();
    }

    /**
     * Sets the current index.
     * @param i the new current index
     */
    public void setCurrentIndex(int i) {
        this.tabSwitched(i);

        if (_listener != null) {
            _listener.fieldChanged(this, i);
        }
    }

    /**
     * The <code>Bitmap</code>'s should be in the format so that they can be tiled, the resulting
     * bitmap will end up being <code>[ getWidth(), _labelHeight ]</code> big.
     *
     * @param focused   the focused tile
     * @param unfocused the unfocused tile
     */
    public void setHeaderBackground(Bitmap focused, Bitmap unfocused) {
        _focusedTile = focused;
        _unfocusedTile = unfocused;
        this.makeSpinnerBackground(focused, unfocused);
    }

    /**
     * Only the left and right portions of the fade <code>Bitmap</code>s will be drawn if the they are wider than <code>this.width</code>
     *
     * @param focused   the focused fade
     * @param unfocused the unfocused fade
     */
    public void setHeaderFade(Bitmap focused, Bitmap unfocused) {
        _fade = unfocused;
        _focusedFade = focused;
    }

    /**
     * Sets the label titles.
     * @param tabTitles the new tab titles
     */
    public void setLabelTitles(String[] tabTitles) {
        if (tabTitles.length != _tabContents.size()) {
            throw new IllegalArgumentException("tab titles length does not match tabContents");//$NON-NLS-1$
        }
        _tabTitles.setSize(0);
        for (int i = 0; i < tabTitles.length; ++i) {
            _tabTitles.addElement(tabTitles[i]);
        }
        calculateLabelDimensions();
    }


    //
    // manager containing the tab contact fields
    //

    /**
     * Sets the listener.
     * @param listener the new listener
     */
    public void setListener(FieldChangeListener listener) {
        _listener = listener;
    }

    ;


    /**
     * sets the colour of the currentIndex header label.
     * @param i RGB color
     */
    public void setTextColour(int i) {
        _textColour = i;
    }


    /**
     * Sets the unselected text colour for the headers ( the ones to the left and right of currentIndex ).
     * @param i RGB colour
     */
    public void setUnselectedTextColour(int i) {
        _unselectedTextColor = i;
    }


    /**
     * Fades the sides from ALPHA_FADE to 255 linearly.  Blends against the colour black
     * @param b the bitmap to fade
     */
    protected void applyFade(Bitmap b) {
        int[] rgb = new int[b.getWidth() * b.getHeight()];

        b.getARGB(rgb, 0, b.getWidth(), 0, 0, b.getWidth(), b.getHeight());
        float a = ALPHA_FADE;
        float a_add = (255.0f - ALPHA_FADE) / FADE_WIDTH;
        for (int i = 0; i < FADE_WIDTH; ++i) {
            for (int j = 0; j < b.getHeight(); ++j) {
                int c = rgb[j * b.getWidth() + i];
                c &= 0xFFFFFF;
                rgb[j * b.getWidth() + i] = blend(c, (int) a);
            }
            a += a_add;
            if (a > 255.0f)
                a = 255.0f;
        }

        a = ALPHA_FADE;
        for (int i = b.getWidth() - 1; i > (b.getWidth() - FADE_WIDTH); --i) {
            for (int j = 0; j < b.getHeight(); ++j) {
                int c = rgb[j * b.getWidth() + i];
                c &= 0xFFFFFF;
                rgb[j * b.getWidth() + i] = blend(c, (int) a);
            }
            a += a_add;
            if (a > 255.0f)
                a = 255.0f;

        }

        b.setARGB(rgb, 0, b.getWidth(), 0, 0, b.getWidth(), b.getHeight());
    }


    /**
     * Draw focus.
     * @param g the graphics context
     * @param on isFocus?
     */
    protected void drawFocus(Graphics g, boolean on)
    {
        if (on) {
            this.subpaint(g);
        }
    }

    /**
     * this does the actual expansion of the graphics, as well as applying the fade to each side.
     * @param focused focused tile
     * @param unfocused unfocused tile
     */
    protected void makeSpinnerBackground(Bitmap focused, Bitmap unfocused) { // call this after layout

        if (_labelHeight == 0 || _labelWidth == 0) {
            // we haven't been layed out yet
            return;
        }

        int w = getWidth();
        _background = new Bitmap(w, _labelHeight);

        // if the widths aren't even length there's a chance we will miss a pixel
        int len = (w + (unfocused.getWidth() >> 1)) / unfocused.getWidth();

        for (int i = 0; i < len; ++i) {
            blt(unfocused, 0, 0, unfocused.getWidth(), _labelHeight, _background, i * unfocused.getWidth(), 0);
        }

        _focusedBackground = new Bitmap(w, _labelHeight);
        len = (w + (focused.getWidth() >> 1)) / focused.getWidth();

        for (int i = 0; i < len; ++i) {
            blt(focused, 0, 0, focused.getWidth(), _labelHeight, _focusedBackground, i * focused.getWidth(), 0);
        }

        applyFade(_background);
        applyFade(_focusedBackground);
    }

    /**
     * dx > 0 - next dx < 0 - previous
     * <p/>
     * else super.
     * @param dx the dx
     * @param dy the dy
     * @param status the status
     * @param time the time
     * @return true, if successful
     */
    protected boolean navigationMovement(final int dx, final int dy, final int status, final int time) {
        _direction = dx > 0 ? 1 : -1;

        //todo: simplify this logic
        if (dx != 0 && dy == 0) {
            if (this.getField(0).isFocus()) {
                return handleSwipe(dx, time);
            }
        } else if (this.getField(0).isFocus() && dy > 0) {
            _contentManager.getField(getTargetIndex() + 1).setFocus();
            return true;
        }

        return super.navigationMovement(dx, dy, status, time);
    }
    
    public void right() {
    	if (this.getField(0).isFocus()) {
            handleSwipe(1, 0);
        }
    }
    
    public void left() {
    	if (this.getField(0).isFocus()) {
            handleSwipe(-1, 0);
        }
    }

    /* (non-Javadoc)
     * @see net.rim.device.api.ui.Field#onDisplay()
     */
    protected void onDisplay() {
        super.onDisplay();

        if (_invalidateRunnable == null) {
            _invalidateRunnable = new InvalidateRunnable();
        }
    }

    /* (non-Javadoc)
     * @see net.rim.device.api.ui.Field#onObscured()
     */
    protected void onObscured() {
        super.onObscured();
        kickToListEdge();
    }

    /**
     * paints the tab header.
     * @param g the graphics context
     */
    protected void paintHeaderLabels(Graphics g) {
        if (_focusedTile != null && _unfocusedTile != null) {
            if (_background == null || _background.getWidth() != getWidth()) {
                makeSpinnerBackground(_focusedTile, _unfocusedTile);
            }
            g.drawBitmap(0, 0, _background.getWidth(), _background.getHeight(),
                    (this.getField(0).isFocus()) ? _focusedBackground : _background, 0, 0);
        }

        int x = getWidth() >> 1;

        int currentValue = _xInterpolator.getCurrentValue();
        int currentIndex = currentValue / _labelWidth;

        x -= currentValue % _labelWidth;

        int totalLabelWidth = _labelWidth * (_tabContents.size());
        int contentWidth = _contentManager.getVirtualWidth() - _contentManager.getWidth();
        int contentValue = currentValue * contentWidth / (totalLabelWidth != 0 ? totalLabelWidth : 1);
        contentValue += _contentManager.getWidth();

        // centre the label within the height that's been allocated for it
        int y = (_labelHeaderHeight - _labelHeight >> 1);

        // start from the currentIndex, go left until we find that the string is offscreen
        int left = currentIndex - 1;
        if (left < 0) {
            left = 0;
        } else {
            x -= _labelWidth;
        }

        // this should be able to be doen with one calculation
        while (left > 0 && (x - _labelWidth >> 1) > 0) {
            left--;
            x -= _labelWidth;
            if (left < 0) {
                left = 0;
                break;
            }
        }

        // draw the labels
        for (int i = 0; i < _tabTitles.size() && left < _tabTitles.size(); ++i) {
            paintLabel(g, _wrappedTitles[left], x, y, Graphics.HCENTER, left == currentIndex);
            x += _labelWidth;
            left = (left + 1);
            if (x > getWidth()) {
                break;
            }
        }

        // draw fade in foreground
        if (_focusedFade != null && _fade != null) {
            g.drawBitmap(0, 0, getWidth() / 2, _fade.getHeight(),
                    (this.getField(0).isFocus()) ? _focusedFade : _fade, 0, 0);
            g.drawBitmap(getWidth() / 2, 0, getWidth() / 2, _fade.getHeight(),
                    (this.getField(0).isFocus()) ? _focusedFade : _fade,
                            _fade.getWidth() - getWidth() / 2, 0);
        }

        // workaround to draw a separator field at where the height of the shown font is
        g.pushContext(0, 0, getWidth(), getHeight(), 0, 0);
        g.setColor(SEPARATOR_COLOR);
        g.setGlobalAlpha(SEPARATOR_ALPHA);
        g.drawLine(0, _labelHeaderHeight - 1, getWidth(), _labelHeaderHeight - 1);
        g.popContext();
    }


    /**
     * Paint label.
     * @param g graphics context
     * @param label the current label
     * @param x where to paint
     * @param y where to paint
     * @param flag flags to pass to g.drawText
     * @param isSelectedIndex current index
     */
    protected void paintLabel(Graphics g, String[] label, int x, int y, int flag, boolean isSelectedIndex) {
        int tc = g.getColor();
        Font f = getFont();
        x -= _labelWidth >> 1;

        for (int i = 0; i < label.length; ++i) {

            if (isSelectedIndex) {
                g.setColor(_textColour);
            } else {
                g.setColor(_unselectedTextColor);
            }
            g.drawText(label[i], x, y, Graphics.ELLIPSIS | flag, _labelWidth);
            y += f.getHeight();
        }
        g.setColor(tc);
    }


    /**
     * does the layout, calculates label dimensions.
     * @param w the w
     * @param h the h
     */
    protected void sublayout(int w, int h)
    {
        calculateLabelDimensions(w, h);

        if (_labelWidth != 0) {
            kickToListEdge();
            animate();
        }
        super.sublayout(w, h);

    }


    /**
     * Main workhorse method, everything is drawn here.
     * @param g graphics context
     */
    protected void subpaint(Graphics g)
    {
        if (!_xInterpolator.isDone()) {
            animate();
        } else {
            _xInterpolator.set(_xInterpolator.getFinalValue());
            int targetIndex = getTargetIndex();
            if (!_eventFired && (targetIndex != _currentIndex)) {

                tabSwitched(targetIndex);
                _currentIndex = targetIndex;

                if (_listener != null) {
                    _listener.fieldChanged(this, targetIndex);
                }

                _eventFired = true;
            }
        }
        super.subpaint(g);
    }

    /**
     * override this for notification when a tab change occurs.
     *
     * @param newIndex the new tab we're switching to
     */
    protected void tabSwitched(int newIndex) {

    }


    //#ifndef VER_4.6.0
    /* (non-Javadoc)
     * @see net.rim.device.api.ui.Manager#touchEvent(net.rim.device.api.ui.TouchEvent)
     */
    protected boolean touchEvent(net.rim.device.api.ui.TouchEvent message) {
        if (message == null) {
            throw new IllegalArgumentException();
        }

        int x = message.getX(1);
        int y = message.getY(1);


        int dx = _previousTouchX - x;

        if (_scrollDir == HORIZ && (message.getEvent() == net.rim.device.api.ui.TouchEvent.UP || y < 0)) {

            _scrollDir = NO_DIR;

            dx = dx * _labelWidth / getWidth();

            _xInterpolator.kick(dx);
            _xInterpolator.kick(_direction * _labelWidth >> 1);
            kickToListEdge();
            animate();
        }

        return super.touchEvent(message);
    }
    //#endif


    /**
     * Animate.
     */
    private void animate() {
        if (_eventFired) {
            _eventFired = false;
        }

        if (_invalidateRunnable != null) {
            _invalidateRunnable.schedule();
        }
    }


    /**
     * sets _labelWidth and _labelHeight, also splits the strings up based on the optimal _labelWidth.
     */
    private void calculateLabelDimensions() {
        calculateLabelDimensions(getWidth(), getHeight());
    }

    /**
     * Calculate label dimensions.
     * @param width the width
     * @param height the height
     */
    private void calculateLabelDimensions(int width, int height) {
        int maxWidth = 0;
        Font f = getFont();
        for (int i = 0; i < _tabTitles.size(); ++i) {
            maxWidth = Math.max(maxWidth, f.getAdvance((String) _tabTitles.elementAt(i)));
        }


        _labelWidth = (maxWidth > width) ? width : maxWidth;

        if ((_labelWidth * _tabTitles.size()) < width) {
            _labelWidth = width / _tabTitles.size();
        }

        if (_labelWidth < width) {
            _labelWidth = width * 45 / 100;
        }


        if (maxWidth > _labelWidth) {
            _labelHeight = f.getHeight() * 2 + 3;
        } else {
            _labelHeight = f.getHeight() + 3;
        }
        if (_focusedTile != null) {
            // should scale the source graphic here
            if (_focusedTile.getHeight() > _labelHeight) {
                _labelHeight = _focusedTile.getHeight();
            } else if (_focusedTile.getHeight() < _labelHeight)
            {
                Bitmap temp = new Bitmap( _unfocusedTile.getWidth(), _labelHeight );
                _unfocusedTile.scaleInto(temp, Bitmap.FILTER_LANCZOS);
                _unfocusedTile = temp;

                temp = new Bitmap( _focusedTile.getWidth(), _labelHeight );
                _focusedTile.scaleInto(temp, Bitmap.FILTER_LANCZOS);
                _focusedTile = temp;
            }
        }

        _wrappedTitles = new String[_tabTitles.size()][];

        for (int i = 0; i < _tabTitles.size(); ++i) {
            String[] s = wrapString((String) _tabTitles.elementAt(i), _labelWidth, getFont());
            _wrappedTitles[i] = s;
        }
        int oldMax = _xInterpolator.getMax();
        _xInterpolator.setBounds(0, _labelWidth * (_tabTitles.size() - 1));
        // adjust the current value if any changes to the bounds
        if (oldMax > 0 && _xInterpolator.getCurrentValue() > 0 && _xInterpolator.getMax() != oldMax) {
            _xInterpolator.set(_xInterpolator.getMax() * _xInterpolator.getCurrentValue() / oldMax);
        }
    }

    /**
     * Ensure that we're on a list edge. This usually finishes off a movement sequence
     * on a touch device, or is called from sublayout or other methods to ensure that
     * the manager is never stuck in a state "in between" lists.
     */

    private void kickToListEdge() {

        int xDestination = _xInterpolator.getFinalValue();
        int xOffset = xDestination % _labelWidth;

        if (xOffset != 0 && xDestination < _xInterpolator.getMax()) {
            if (xOffset > _labelWidth >> 1) {
                _xInterpolator.kickTo(xDestination + _labelWidth - xOffset);
            } else {
                _xInterpolator.kickTo(xDestination - xOffset);
            }
        }
    }

    /**
     * Wrap string.
     * @param s the string to wrap
     * @param w the width in pixels to wrap to
     * @param f the font so we can measure width ( pass null for the component default )
     * @return an array with each new line on a new array element
     */
    public static String[] wrapString(String s, int w, Font f) {
        int offset = 0;
        Vector v = new Vector();

        do {
            int len = s.length() - offset;

            if (f.getAdvance(s, offset, len) > w)
            {
                //This loop finds where to wrap the line
                while ((f.getAdvance(s, offset, len) > w)) {
                    len--;
                    if (len < 1) {
                        len = 1;
                        break;
                    }
                    int t_len = len;


                    for (; len >= 0; --len) {
                        if (s.charAt(offset + len) == ' ') {
                            t_len = len;


                            if (t_len < 1) {
                                t_len = 1;
                            }
                            break;
                        }
                    }

                    len = t_len;
                }
            }

            v.addElement(s.substring(offset, offset + len));

            offset += len;
            if (offset < s.length() && s.charAt(offset) == ' ') {
                offset++;
            }
        } while (offset < s.length());
        String[] r = new String[v.size()];
        v.copyInto(r);
        return r;
    }


    /**
     * Special case alpha blending for against black.
     * @param c colour to blend (RGB)
     * @param a alpha value 0-255
     * @return the blended colour
     */
    private static final int blend(int c, int a) {
        int r = (((c >>> 16) & 0xff) * a) >> 8;
        int g = (((c >>> 8) & 0xff) * a) >> 8;
        int b = (((c & 0xff) * a) >> 8);

        return (0xff << 24) | r << 16 | g << 8 | b;

    }

    /**
     * Blt.
     * @param src source Bitmap
     * @param x source X
     * @param y source Y
     * @param w source width ( also dest. width )
     * @param h sourc height
     * @param dst destination bitmap
     * @param dx destination x
     * @param dy destination y
     */
    private static void blt(Bitmap src, int x, int y, int w, int h, Bitmap dst, int dx, int dy) {
        int[] src_rgb = new int[src.getWidth() * src.getHeight()];
        src.getARGB(src_rgb, 0, src.getWidth(), 0, 0, src.getWidth(), src.getHeight());
        int[] dst_rgb = new int[dst.getWidth() * dst.getHeight()];
        dst.getARGB(dst_rgb, 0, dst.getWidth(), 0, 0, dst.getWidth(), dst.getHeight());


        int srcPos = y * w + x;
        int dstPos = dy * dst.getWidth() + dx;

        for (int i = 0; i < h; ++i) {
            for (int j = 0; j < w; ++j) {
                dst_rgb[dstPos + j] = src_rgb[srcPos + j];
            }
            srcPos += w;
            dstPos += dst.getWidth();
        }

        dst.setARGB(dst_rgb, 0, dst.getWidth(), 0, 0, dst.getWidth(), dst.getHeight());
    }

    /**
     * The Class InvalidateRunnable.
     */
    private class InvalidateRunnable implements Runnable {

        /** The _scheduled. */
        private boolean _scheduled;

        /** The _ui application. */
        private UiApplication _uiApplication = UiApplication.getUiApplication();

        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        public void run() {
            synchronized (this) {
                _scheduled = false;
            }

            TabManager.this.invalidate();
        }

        /**
         * Schedule.
         */
        public void schedule() {
            synchronized (this) {
                if (_scheduled || _uiApplication == null) {
                    return;
                }
                _scheduled = true;
            }
            _uiApplication.invokeLater(this);
        }
    }

    /**
     * The Class SpacerField.
     */
    private class SpacerField extends Field {

        /* (non-Javadoc)
         * @see net.rim.device.api.ui.Field#layout(int, int)
         */
        public void layout(int w, int h) {
            setExtent(w, h);
        }

        /* (non-Javadoc)
         * @see net.rim.device.api.ui.Field#paint(net.rim.device.api.ui.Graphics)
         */
        public void paint(Graphics g) {
        }
    }

    /**
     * The Class TabContentManager.
     */
    private class TabContentManager extends AbsoluteFieldManager {

        /* (non-Javadoc)
         * @see net.rim.device.api.ui.Manager#add(net.rim.device.api.ui.Field)
         */
        public void add(Field f) {
            this.add(f, getFieldCount() * getWidth(), 0);
        }

        /* (non-Javadoc)
         * @see net.rim.device.tabmanager.bbcommons.ui.component.AbsoluteFieldManager#nextFocus(int, int)
         */
        protected int nextFocus(int dir, int axis) {
            // we don't want the default focus behaviour or
            // we go off the end of the content area

            if (axis == AXIS_HORIZONTAL) {
                return super.nextFocus(dir, axis);
            } else {
                return -1;
            }
        }

        /* (non-Javadoc)
         * @see net.rim.device.tabmanager.bbcommons.ui.component.AbsoluteFieldManager#sublayout(int, int)
         */
        protected void sublayout(int w, int h)
        {
            for (int i = 0; i < getFieldCount(); ++i) {
                setPositionChild(getField(i), i * w, 0);
                layoutChild(getField(i), w, h);
            }

            this.setExtent(w, h);
        }


        /* (non-Javadoc)
         * @see net.rim.device.api.ui.Manager#subpaint(net.rim.device.api.ui.Graphics)
         */
        protected void subpaint(Graphics g)
        {
            int currentValue = _xInterpolator.getCurrentValue();
            int totalLabelWidth = _labelWidth * _tabContents.size();
            int contentWidth = _contentManager.getWidth() * _tabContents.size();
            int contentValue = currentValue * contentWidth / (totalLabelWidth != 0 ? totalLabelWidth : 1);
            contentValue += getWidth();
            int x = MathUtilities.clamp(0, contentValue, contentWidth + getWidth());

            for (int i = 0; i < getFieldCount(); ++i) {
                setPosChild(getField(i), -x + i * getWidth(), 0);
                if ((-x + (i + 1) * getWidth()) > 0 && (-x + i * getWidth() < getWidth())) {
                    paintChild(g, getField(i));
                }
            }

        }


        //#ifndef VER_4.6.0
        /* (non-Javadoc)
         * @see net.rim.device.api.ui.Manager#touchEvent(net.rim.device.api.ui.TouchEvent)
         */
        protected boolean touchEvent(net.rim.device.api.ui.TouchEvent message) {
            if (message == null) {
                throw new IllegalArgumentException();
            }

            int x = message.getX(1);
            int y = message.getY(1);


            int dx = _previousTouchX - x;
            int dy = _previousTouchY - y;

            switch (message.getEvent()) {

                case net.rim.device.api.ui.TouchEvent.DOWN:
                    if (x < 0 || y < 0 || y > getHeight()) {
                        return super.touchEvent(message);
                    }
                    _previousTouchX = x;
                    _previousTouchY = y;
                    _xInterpolator.set(_xInterpolator.getCurrentValue());
                    _scrollDir = NO_DIR;
                    animate();

                    break;


                case net.rim.device.api.ui.TouchEvent.MOVE:
                    if (x < 0 || y < 0 || y > getHeight()) {
                        return super.touchEvent(message);
                    }

                    if (_scrollDir == NO_DIR) {
                        if (Math.abs(dx * 2 / 3) > Math.abs(dy)) {
                            _scrollDir = HORIZ;
                        } else {
                            _scrollDir = VERT;
                        }
                    }

                    if (_scrollDir == HORIZ) {
                        dx = dx * _labelWidth / getWidth();
                        _xInterpolator.kick(dx);
                        _previousTouchX = x;

                        animate();
                        return true;
                    }

                    break;
            }

            return super.touchEvent(message);
        }
    }
    //#endif


    /**
     * A little helper class so that the focus works correctly.
     */
    private class TabHeader extends Field {

        /* (non-Javadoc)
         * @see net.rim.device.api.ui.Field#isFocusable()
         */
        public boolean isFocusable() {
            return true;
        }

        /* (non-Javadoc)
         * @see net.rim.device.api.ui.Field#layout(int, int)
         */
        protected void layout(int availableWidth, int availableHeight) {
            // add some 'whitespace' above and below the header label
            int height = Math.min(getXHeight(), availableHeight);
            _labelHeaderHeight = height;
            setExtent(availableWidth, height);

        }

        /* (non-Javadoc)
         * @see net.rim.device.api.ui.Field#paint(net.rim.device.api.ui.Graphics)
         */
        protected void paint(Graphics g) {
            paintHeaderLabels(g);
        }

        /* (non-Javadoc)
         * @see net.rim.device.api.ui.Field#paintBackground(net.rim.device.api.ui.Graphics)
         */
        protected void paintBackground(Graphics graphics){
            super.paintBackground(graphics);
            graphics.setBackgroundColor(Color.GRAY);
            graphics.clear();
        }


        //#ifndef VER_4.6.0
        /* (non-Javadoc)
         * @see net.rim.device.api.ui.Field#touchEvent(net.rim.device.api.ui.TouchEvent)
         */
        protected boolean touchEvent(net.rim.device.api.ui.TouchEvent message) {
            if (message == null) {
                throw new IllegalArgumentException();
            }

            int x = message.getX(1);
            int y = message.getY(1);

            if (x < 0 || y < 0 || y > getHeight()) {
                return super.touchEvent(message);
            }

            int dx = _previousTouchX - x;

            switch (message.getEvent()) {

                case net.rim.device.api.ui.TouchEvent.DOWN:
                    _scrollDir = NO_DIR;
                    _previousTouchX = x;
                    _xInterpolator.set(_xInterpolator.getCurrentValue());

                    animate();
                    break;


                case net.rim.device.api.ui.TouchEvent.MOVE:
                    _xInterpolator.kick(dx);
                    _previousTouchX = x;
                    // assumption: if movement in narrow title bar, then it is horizontal.
                    _scrollDir = HORIZ;
                    animate();
                    return true;

                case net.rim.device.api.ui.TouchEvent.UP:
                    if (_scrollDir == NO_DIR) {
                        if (x < 40 && getTargetIndex() > 0) {
                            _xInterpolator.kick(-_labelWidth);
                            animate();
                        } else if (x > (getWidth() - 40) && getTargetIndex() < (_tabContents.size() - 1)) {
                            _xInterpolator.kick(_labelWidth);
                            animate();
                        }
                        return true;
                    }

            }

            return true;
        }
        //#endif


        /**
         * Gets the x height.
         * @return the x height
         */
        private int getXHeight() {
            boolean touchSupported = false;
            //#ifndef VER_4.6.0
            touchSupported = net.rim.device.api.ui.Touchscreen.isSupported();
            //#endif

            int heightFactor = 2;
            if (touchSupported) {
                heightFactor = (Display.getHeight() > Display.getWidth()) ? 4 : 3;
            }
            return (_labelHeight * heightFactor) >> 1;
        }
    }
}