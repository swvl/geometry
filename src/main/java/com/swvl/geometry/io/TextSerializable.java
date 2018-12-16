/***********************************************************************
 * Copyright (c) 2015 by Regents of the University of Minnesota.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0 which
 * accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 *
 *************************************************************************/
package com.swvl.geometry.io;

import org.apache.hadoop.io.Text;

/**
 * Interface for objects conversion from or to text .
 *
 * @author Hatem Morgan
 */
public interface TextSerializable {
    /**
     * Store current object as string in the given text appending text already there.
     *
     * @param text The text object to append to.
     * @return The same text that was passed as a parameter
     */
    public Text toText(Text text);

    /**
     * Retrieve information from the given text.
     *
     * @param text The text to parse
     */
    public void fromText(Text text);
}
