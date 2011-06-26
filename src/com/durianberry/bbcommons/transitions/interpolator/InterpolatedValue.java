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



public class InterpolatedValue extends AdjustableInterpolatedValue {

    InterpolationInterval _interpolationInterval;

    public InterpolatedValue() {
        _interpolationInterval = new InterpolationInterval();
    }
    
    public InterpolatedValue( int acceleration ) {
        _interpolationInterval = new InterpolationInterval( acceleration );
    }

    public void kickTo( int position ) {
        kickTo( _interpolationInterval.getCurrentValue(), position );
    }

    public void kick( int distance ) {
        kick( _interpolationInterval.getCurrentValue(), distance );
    }

    protected void kick() {
        _interpolationInterval.kick( _distance );
        super.kick();
    }

    public void flick() {
        flick( _interpolationInterval.getCurrentValue() );
    }

    public int getCurrentValue() {
        return getCurrentValue( _interpolationInterval.getCurrentValue() );
    }

    public int getCurrentVelocity() {
        return getCurrentVelocity( _interpolationInterval.getCurrentValue() );
    }

    public boolean isDone() {
        return _interpolationInterval.isDone();
    }
    
    public void enforceBounds() {
        if( outOfBounds( getFinalValue() ) ) {
            // we are out of bounds so correct it
            if( outOfBounds( getCurrentValue() ) ) {
                // if current and target values are out of bounds then kick it to the boundary
                kickTo( clamp( getFinalValue() ) );
            } else {
                // if only the target is out of bounds then we some momemtum so just use snap to adjust the target to the boundary
                snap();
            }
        }
    }

    
}

