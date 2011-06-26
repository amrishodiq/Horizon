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

package com.durianberry.bbcommons.transitions.interpolator;

import net.rim.device.api.math.Fixed32;

/**
 * @category Internal Signed
 */
public class InterpolationInterval {

    private long _start;
    private int _duration;

    // at a = 10 a 10 unit move takes 141 msec and a 100 unit move takes 447 msec (quicker)
    // at a = 15 a 10 unit move takes 173 msec and a 100 unit move takes 547 msec
    // at a = 20 a 10 unit move takes 200 msec and a 100 unit move takes 632 msec (slower)
    public static final int DEFAULT_ACCELERATION = 5;

    private int _acceleration; // (a) in fixed32 units per second per second

    public InterpolationInterval() {
        this( DEFAULT_ACCELERATION );
    }
    public InterpolationInterval( int acceleration ) {
        // convert to units per millisecond per millisecond
        _acceleration = Fixed32.tenThouToFP( acceleration );
    }

    public void kick( int dx, int dy ) {
        kick( (int) Math.sqrt( dx*dx + dy * dy ) );
    }
    public void kick( int distance ) {
        _start = System.currentTimeMillis();

        // t = sqrt( -2as ) see http://en.wikipedia.org/wiki/Equations_of_Motion
        // and convert the result from seconds in Fixed32 to milliseconds as an integer
        _duration = Fixed32.sqrt( 2 * _acceleration * Math.abs( distance ) ) * 1000 / Fixed32.ONE;
    }

    public int getCurrentValue() {
        long t = System.currentTimeMillis() - _start;
        if( t >= _duration ) {
            return Fixed32.ONE;
        }

        // convert time to a fraction of the duration in Fixed32
        return (int) ( t * Fixed32.ONE / _duration );
    }

    public boolean isDone() {
        return ( System.currentTimeMillis() - _start ) > _duration;
    }
}

