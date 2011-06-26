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
import net.rim.device.api.util.MathUtilities;

public class DependentInterpolatedValue {

    int _anchor;
    int _target;
    int _distance;

    int _v0;
    int _c2;
    int _c3;

    int _min = Integer.MIN_VALUE;
    int _max = Integer.MAX_VALUE;

    public DependentInterpolatedValue() {
    }
    public DependentInterpolatedValue( int min, int max ) {
        setBounds( min, max );
    }

    public int getMin() {
        return _min;
    }

    public int getMax() {
        return _max;
    }

    public void setBounds( int min, int max ) {
        _min = min;
        _max = max;
    }

    public boolean outOfBounds( int value ) {
        return value < _min || value > _max;
    }

    public int clamp( int position ) {
        return MathUtilities.clamp( _min, position, _max );
    }

    public int getFinalValue() {
        return _target;
    }

    public void set( int value ) {
        _anchor = _target = clamp( value );
        _distance = _v0 = _c2 = _c3 = 0;
    }
    
    public void flick (int time) {
        flick(time, 10);
    }

    public void flick( int time, int rebound ) {
        // increase the distance to travel based on the current velocity
        kick( time, getCurrentVelocity( time ) * rebound / 10 );
    }

    public void kick( int time, int distance ) {
        kickTo( time, _target + distance );
    }

    public void kickTo( int time, int position ) {
        _anchor = getCurrentValue( time );
        _distance = position - _anchor;
        _target = position;
        kick();
    }
    
    protected void kick() {
        kickWithFriction(2);
    }

    protected void kickWithFriction(int friction) {
        // the initial velocity (v0) = 2s
        _v0 = friction * _distance * Fixed32.ONE;

        snap();
    }

    public void snap() {
        snapTo( clamp( _target ) );
    }

    public void snapTo( int target ) {
        int distance = ( target - _anchor ) * Fixed32.ONE;
        _c2 = -2 * _v0 + 3 * distance;
        _c3 = _v0 - 2 * distance;
        _target = target;
    }

    public int distance() {
        return _distance;
    }
    
    public int getCurrentVelocity( int t ) {
        int t2 = Fixed32.mul( t, t );
        return ( _v0 + 2 * Fixed32.mul( _c2, t ) + 3 * Fixed32.mul( _c3, t2 ) ) / Fixed32.ONE;
    }
    
    public int getCurrentValue( int t ) {
        int t2 = Fixed32.mul( t, t );
        int t3 = Fixed32.mul( t2, t );
        return _anchor + ( Fixed32.mul( _v0, t ) + Fixed32.mul( _c2, t2 ) + Fixed32.mul( _c3, t3 ) ) / Fixed32.ONE;
    }
}
