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

import net.rim.device.api.util.MathUtilities;

/**
 * @category Internal
 */
class AdjustableInterpolatedValue extends DependentInterpolatedValue {
    int _friction = -1;
    int _rebound = -1;

    public AdjustableInterpolatedValue() {
    }
    
    public void flick( int time ) {
        flick(time, _rebound > -1 ? _rebound : 10);
    }

    protected void kick() {
        kickWithFriction(_friction > -1 ? _friction : 2);
    }
    
    public void setFriction(int friction) {
        _friction = MathUtilities.clamp(0, friction, 10);
    }

    public void setRebound(int rebound) {
        _rebound = MathUtilities.clamp(1, rebound, 10);
    }
}
