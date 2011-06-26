/*
 * JustifiedVerticalFieldManager.java
 *
 * © Research In Motion Limited, 2008
 * Confidential and proprietary.
 */

package com.durianberry.weather.view;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.util.*;

public class JustifiedVerticalFieldManager extends Manager
{
    private static final int SYSTEM_STYLE_SHIFT = 32;
    
    public Field _topField;
    public Field _centerField;
    public Field _bottomField;
    
    protected XYEdges _topFieldMargins = new XYEdges();
    protected XYEdges _centerFieldMargins = new XYEdges();
    protected XYEdges _bottomFieldMargins = new XYEdges();


    public JustifiedVerticalFieldManager( long style )
    {
        super( style );
    }

    public JustifiedVerticalFieldManager( Field topField, Field centerField, Field bottomField )
    {
        this( topField, centerField, bottomField, Field.USE_ALL_HEIGHT );
    }

    public JustifiedVerticalFieldManager( Field topField, Field centerField, Field bottomField, long style )
    {
        super( style );
        
        _topField     = ( topField    != null ) ? topField    : new NullField( Field.NON_FOCUSABLE );
        _centerField  = ( centerField != null ) ? centerField : new NullField( Field.NON_FOCUSABLE );
        _bottomField  = ( bottomField != null ) ? bottomField : new NullField( Field.NON_FOCUSABLE );

        add( _topField );
        add( _centerField );
        add( _bottomField );
    }
    
    public int getPreferredWidth()
    {
        return   _topField.getPreferredWidth() + _centerField.getPreferredWidth() + _bottomField.getPreferredWidth() 
               + FieldDimensionUtilities.getBorderAndPaddingWidth( this );
    }
    
    public int getPreferredHeight()
    {
        return   Math.max( Math.max( _topField.getPreferredHeight(), _centerField.getPreferredHeight() ), _bottomField.getPreferredHeight() ) 
               + FieldDimensionUtilities.getBorderAndPaddingHeight( this );
    }
    
    protected void sublayout( int width, int height )
    {
        int maxWidth = 0;

        _topField.getMargin(    _topFieldMargins    );
        _centerField.getMargin( _centerFieldMargins );
        _bottomField.getMargin( _bottomFieldMargins );
        
        int topCenterMargin    = Math.max( _topFieldMargins.bottom,    _centerFieldMargins.top );
        int bottomCenterMargin = Math.max( _centerFieldMargins.bottom, _bottomFieldMargins.top );
        
        int availableHeight = height - ( _topFieldMargins.top + topCenterMargin + bottomCenterMargin + _bottomFieldMargins.bottom );

        layoutChild( _topField, width - _topFieldMargins.left - _topFieldMargins.right, availableHeight );
        maxWidth = Math.max( maxWidth, _topFieldMargins.left + _topField.getWidth() + _topFieldMargins.right );
        availableHeight -= _topField.getHeight();
        
        layoutChild( _bottomField, width - _bottomFieldMargins.left - _bottomFieldMargins.right, availableHeight  );
        maxWidth = Math.max( maxWidth, _bottomFieldMargins.left + _bottomField.getWidth() + _bottomFieldMargins.right );
        availableHeight -= _bottomField.getHeight();

        layoutChild( _centerField, width - _centerFieldMargins.left - _centerFieldMargins.right, availableHeight  );
        maxWidth = Math.max( maxWidth, _centerFieldMargins.left + _centerField.getWidth() + _centerFieldMargins.right );
        availableHeight -= _centerField.getHeight();

        if( !isStyle( Field.USE_ALL_WIDTH ) ) {
            width = maxWidth;
        }
        if( !isStyle( Field.USE_ALL_HEIGHT ) ) {
            height -= availableHeight;
        }

        int yTop  = _topFieldMargins.top;
        int yBottom = height - _bottomField.getHeight() - _bottomFieldMargins.bottom;
        int yCenter;
        int yCenterMin = yTop + _topField.getHeight() + topCenterMargin;
        int yCenterMax = yBottom - _centerField.getHeight() - bottomCenterMargin; 
        
        switch( (int)( ( _centerField.getStyle() & FIELD_VALIGN_MASK ) >> SYSTEM_STYLE_SHIFT ) ) {
            case (int)( FIELD_BOTTOM >> SYSTEM_STYLE_SHIFT ):
                yCenter = yCenterMax; 
                break;
            case (int)( FIELD_VCENTER >> SYSTEM_STYLE_SHIFT ):
                yCenter = MathUtilities.clamp( yCenterMin, ( height - _centerField.getHeight() ) / 2, yCenterMax ) ;
                break;
            default: // top
                yCenter = yCenterMin;
                break;
        }
                          
        setPositionChild( _topField,    getFieldX( _topField,    width, _topFieldMargins    ), yTop    );
        setPositionChild( _centerField, getFieldX( _centerField, width, _centerFieldMargins ), yCenter );
        setPositionChild( _bottomField, getFieldX( _bottomField, width, _bottomFieldMargins ), yBottom );
                          
        setExtent( width, height );
    }

    private int getFieldX( Field field, int width, XYEdges margins )
    {
        switch( (int)( ( field.getStyle() & FIELD_HALIGN_MASK ) >> SYSTEM_STYLE_SHIFT ) ) {
            case (int)( FIELD_RIGHT >> SYSTEM_STYLE_SHIFT ):
                return width - field.getWidth() - margins.right; // missing left margin???
            case (int)( FIELD_HCENTER >> SYSTEM_STYLE_SHIFT ):
                return margins.left + ( width - margins.left - field.getWidth() - margins.right ) / 2;
            default:
                return margins.left;
        }
    }

    public Field getTopField()
    {
        return _topField;
    }
    
    public Field getCenterField()
    {
        return _centerField;
    }
    
    public Field getBottomField()
    {
        return _bottomField;
    }
    
    public void replace( Field oldField, Field newField )
    {
        if( oldField == newField ) {
            // Nothing to do
            return;
        }
        
        if( oldField == _topField ) {
            _topField = newField;
        } else if( oldField == _centerField ) {
            _centerField = newField;
        } else if( oldField == _bottomField ) {
            _bottomField = newField;
        } else {
            throw new IllegalArgumentException();
        }
        add( newField );
        delete( oldField );
    }
}    


