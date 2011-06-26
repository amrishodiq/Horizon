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

package com.durianberry.bbcommons.ui.component;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;

/**
 * A manager that allows placement of fields at specific x-y positions. Rather
 * than based on the extent of the surrounding fields, fields can be placed
 * anywhere on the screen and can even overlap.
 *
 * @category Open
 */
public class AbsoluteFieldManager extends Manager {
    /**
     * Max height used
     */
    private static final int MAX_HEIGHT = Integer.MAX_VALUE >> 1;

    /**
     * Creates a new AbsoluteFieldManager.
     *
     * @category Open
     */
    public AbsoluteFieldManager() {
        super(NO_VERTICAL_SCROLL);
    }

    /**
     * Retrieves this field's preferred height.
     *
     * @return Always returns the height of the display.
     * @category Open
     */
    public int getPreferredHeight() {
        return Display.getHeight();
    }

    /**
     * Retrieves this field's preferred width.
     *
     * @return Always returns the width of the display.
     * @category Open
     */
    public int getPreferredWidth() {
        return Display.getWidth();
    }

    /**
     * Finds the index of the field that should receive focus given the
     * direction of motion.
     *
     * @return The next field to receive focus.
     * @throws IllegalArumentException
     *             if axis is not one of AXIS_SEQUENTIAL, AXIS_HORIZONTAL or
     *             AXIS_VERTICAL.
     * @category Experimental
     */
    protected int nextFocus(final int direction, final int axis) {
        Field fieldWithFocus = getFieldWithFocus();
        int fieldCount = getFieldCount();

        if (fieldWithFocus == null) {
            // So far, this case has been encountered only in third-party apps,
            // so we just use the old behaviour.
            return super.nextFocus(direction, axis);
        } else if (fieldCount < 2) {
            return fieldWithFocus.getIndex();
        }

        int fieldWithFocusTop = fieldWithFocus.getTop();
        int fieldWithFocusLeft = fieldWithFocus.getLeft();

        Field fieldToGiveFocus = null;
        Field currField;

        switch (axis) {
        case AXIS_SEQUENTIAL:
        case AXIS_HORIZONTAL:
            for (int i = 0; i < getFieldCount(); i++) {
                currField = getField(i);
                if ((currField.getLeft() - fieldWithFocusLeft) * direction > 0) {
                    fieldToGiveFocus = currField;
                }

            }
            // there are no fields in the direction
            if (fieldToGiveFocus == null) {
                return -1;
            }

            for (int i = 1; i < getFieldCount(); i++) {
                currField = getField(i);

                // if the field is closer to the currently focused field
                // vertically and it is on the correct side horizontally
                if (Math.abs(fieldWithFocusTop - currField.getTop()) <= Math
                        .abs(fieldWithFocusTop - fieldToGiveFocus.getTop())
                        && (currField.getLeft() - fieldWithFocusLeft)
                                * direction > 0) {
                    // if two fields are at the same height then give focus to
                    // the one that is closer horizontally.
                    if (currField.getTop() == fieldToGiveFocus.getTop()) {
                        // only give it focus if it is not the field that
                        // already has focus.
                        if (Math.abs(fieldWithFocusLeft - currField.getLeft()) < Math
                                .abs(fieldWithFocusLeft
                                        - fieldToGiveFocus.getLeft())
                                && currField != fieldWithFocus) {
                            fieldToGiveFocus = currField;
                        }
                    } else {
                        fieldToGiveFocus = currField;
                    }
                }
            }
            break;
        case AXIS_VERTICAL:
            for (int i = 0; i < getFieldCount(); i++) {
                currField = getField(i);
                if ((currField.getTop() - fieldWithFocusTop) * direction > 0) {
                    fieldToGiveFocus = currField;
                }

            }
            // there are no fields in the direction
            if (fieldToGiveFocus == null) {
                return -1;
            }

            for (int i = 1; i < getFieldCount(); i++) {
                currField = getField(i);

                // if the field is closer to the currently focused field
                // horizontally and it is on the correct side vertically
                if (Math.abs(fieldWithFocusLeft - currField.getLeft()) <= Math
                        .abs(fieldWithFocusLeft - fieldToGiveFocus.getLeft())
                        && (currField.getTop() - fieldWithFocusTop) * direction > 0) {
                    // if two fields are at the same x position then give focus
                    // to the one that is closer vertically.
                    if (currField.getLeft() == fieldToGiveFocus.getLeft()) {
                        if (Math.abs(fieldWithFocusTop - currField.getTop()) < Math
                                .abs(fieldWithFocusTop
                                        - fieldToGiveFocus.getTop())
                                && currField != fieldWithFocus) {
                            fieldToGiveFocus = currField;
                        }
                    } else {
                        fieldToGiveFocus = currField;
                    }
                }
            }
            break;
        default:
            throw new IllegalArgumentException("Illegal Axis value: " + axis);
        }

        return fieldToGiveFocus.getIndex();
    }

    /**
     * Overrides Manager#layoutChildren.
     */
    private int layoutChildren(final int maxWidth, final int maxHeight,
            final int heightUsed) {
        int numFields = getFieldCount();
        // the highest y value of displayed field must be returned
        int highestPoint = 0;
        Field field;

        for (int i = numFields - 1; i >= 0; i--) {
            field = getField(i);

            // gather up dimensions of the field so as to avoid too many method
            // calls in the following checks
            int fieldX = field.getLeft();
            int fieldY = field.getTop();
            int fieldWidth = getPreferredWidthOfChild(field);
            int fieldHeight = getPreferredHeightOfChild(field);
            // int right = fieldX + fieldWidth;
            int bottom = fieldY + fieldHeight;

            layoutChild(field, fieldWidth, fieldHeight);
            setPositionChild(field, fieldX, fieldY);

            // check to see if this field occupies the highest y space
            if (bottom > highestPoint) {
                highestPoint = bottom;
            }
        }

        // This is a big ugly hack, fix this properly after word Mole is done
        return Display.getHeight();
    }

    /**
     * Allows a Manager to set the position of one of its Fields.
     *
     * @internal Exposes {@link #setPositionChild(Field, int, int)} as a public
     *           method.
     *
     * @param field
     *            The field to position.
     * @param x
     *            The horizontal position of the field.
     * @param y
     *            The vertical position of the field.
     * @category Open
     */
    public void setPosChild(final Field field, final int x, final int y) {
        setPositionChild(field, x, y);
    }

    /**
     * Implements custom layout features for this manager.
     *
     * @param width
     *            Width available for this manager.
     * @param height
     *            Height available for this manager.
     *
     * @see Manager#sublayout(int, int)
     * @category Open Framework
     */
    protected void sublayout(final int width, final int height) {
        // how much height do we have?
        int heightAvail = height;
        if (isStyle(Manager.VERTICAL_SCROLL)) {
            heightAvail = MAX_HEIGHT;
        }

        // do the layout assuming the determined width
        int virtualHeight = layoutChildren(width, heightAvail, 0);

        // set extents
        // width = calculateWidth( width );
        if (isStyle(Field.USE_ALL_HEIGHT)) {
            setExtent(width, height);
        } else {
            setExtent(width, Math.min(height, virtualHeight));
        }
        setVirtualExtent(width, virtualHeight);
    }

    /**
     * Adds the supplied field to this Manager at the location (x, y).
     *
     * @param field
     *            The field to add.
     * @param x
     *            The horizontal co-ordinate to position the field at.
     * @param y
     *            The vertical co-ordinate to position the field at.
     *
     * @throws IllegalStateException If the field has already been added to a
     * manager.
     * @throws IllegalArgumentException If the field is being added to itself.
     *
     * @category Open
     */
    public void add(final Field field, final int x, final int y) {
        super.add(field);
        setPositionChild(field, x, y);
    }

    /**
     * Inserts a field into this Manager at the location (x, y).
     *
     * @param field
     *            The field to insert.
     * @param index
     *            The index position you wish the new field to occupy in the manager's
     *            list of controlled fields.
     * @param x
     *            The horizontal coordinate to position the field at.
     * @param y
     *            The vertical coordinate to position the field at.
     *
     * @throws IndexOutOfBoundsException If index is less than zero or greater
     * than {@link #getFieldCount}.
     * @throws IllegalStateException If the field has already been added to a
     * manager.
     * @throws IllegalArgumentException If the field is being added to itself.
     *
     * @category Controlled-Internal
     */
    public void insert(final Field field, final int index, final int x, final int y) {
        super.insert(field, index);
        setPositionChild(field, x, y);
    }
}