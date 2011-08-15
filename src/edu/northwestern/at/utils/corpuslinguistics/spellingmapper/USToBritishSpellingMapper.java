package edu.northwestern.at.utils.corpuslinguistics.spellingmapper;

/*	Please see the license information at the end of this file. */

import java.net.*;
import java.io.*;
import java.util.*;

import edu.northwestern.at.utils.*;

/**	Maps American to British spellings.
 */

public class USToBritishSpellingMapper
 	implements SpellingMapper
{
	/**	Resource file with British/American mappings.
	 */

	protected static final String wordMapPath	= "resources/abbc.tab";

	/**	Hash map with American spelling as key and British spelling as value.
	 */

	protected static Map<String,String> USToBritMap	=null;

	/**	Create US to British spelling mapper.
	 */

	public USToBritishSpellingMapper()
	{
								//	Load the US to British word map if
								//	not already loaded.

		if ( USToBritMap == null )
		{
			try
			{
				USToBritMap	= MapFactory.createNewMap();
				loadUSToBritishWordMap();
			}
			catch ( Exception e )
			{
			}
		}
	}

	/**	Load the US to British mapping data.
	 */

	protected void loadUSToBritishWordMap()
		throws MalformedURLException, IOException
	{
								//	URL for British/US mapping word list.
		URL wordMapURL	=
			USToBritishSpellingMapper.class.getResource( wordMapPath );

		BufferedReader mapReader	=
			new BufferedReader
			(
				new UnicodeReader
				(
					wordMapURL.openStream()
				)
			);
                                //	Load hash map with key=US spelling
                                //	and value=British spelling.
                                //	First token on each line is the
                                //	US spelling, followed by an ascii tab,
                                //	followed by the matching British
                                //	spelling.  We ignore the rest of
                                //	the text on each line.

   		String line	= mapReader.readLine();

		while ( line != null )
		{
			StringTokenizer tokens = new StringTokenizer( line , "\t" );

			String usWord	= tokens.nextToken().trim();
			String britWord	= tokens.nextToken().trim();

			USToBritMap.put( usWord , britWord );

			line	= mapReader.readLine();
		}

		mapReader.close();
	}

	/**	Returns British spelling given a U. S. spelling.
	 *
	 *	@param	spelling	The spelling.
	 *
	 *	@return				The mapped spelling.
	 */

	public String mapSpelling( String spelling )
	{
		String result		= spelling;

		String usSpelling	=
			USToBritMap.get( spelling.toLowerCase() );

		if ( usSpelling != null )
		{
			result	= CharUtils.makeCaseMatch( usSpelling , spelling );
		}

		return result;
	}
}

/*
Copyright (c) 2008, 2009 by Northwestern University.
All rights reserved.

Developed by:
   Academic and Research Technologies
   Northwestern University
   http://www.it.northwestern.edu/about/departments/at/

Permission is hereby granted, free of charge, to any person
obtaining a copy of this software and associated documentation
files (the "Software"), to deal with the Software without
restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or
sell copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following
conditions:

    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimers.

    * Redistributions in binary form must reproduce the above
      copyright notice, this list of conditions and the following
      disclaimers in the documentation and/or other materials provided
      with the distribution.

    * Neither the names of Academic and Research Technologies,
      Northwestern University, nor the names of its contributors may be
      used to endorse or promote products derived from this Software
      without specific prior written permission.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE CONTRIBUTORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS WITH THE SOFTWARE.
*/



