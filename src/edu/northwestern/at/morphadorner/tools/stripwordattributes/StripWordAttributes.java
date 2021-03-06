package edu.northwestern.at.morphadorner.tools.stripwordattributes;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;

import edu.northwestern.at.morphadorner.WordAttributeNames;
import edu.northwestern.at.morphadorner.WordAttributePatterns;
import edu.northwestern.at.utils.PatternReplacer;
import edu.northwestern.at.utils.UnicodeReader;

/**	Create derived MorphAdorner file with word elements stripped of  attributes.
 *
 *	<p>
 *	Usage:
 *	</p>
 *
 *	<blockquote>
 *	<pre>
 *	java edu.northwestern.at.morphadorner.tools.stripwordattributes.StripWordAttributes input.xml output.xml output.tab [/[no]id] [/[no]trim]
 *	</pre>
 *	</blockquote>
 *
 *	<table>
 *	<tr>
 *	<td>input.xml</td><td>Input MorphAdornerd xml file.</td>
 *	</tr>
 *	<tr>
 *	<td>output.xml</td>
 *	<td>Derived adorned file with word element attributes stripped.</td>
 *	<tr>
 *	<td>output.tab</td>
 *	<td>Tab delimited file of word element attribute values. </td>
 *	</tr>
 *	<tr>
 *	<td>/id	or /noid</td>
 *	<td>Optional parameter indicating xml:id should be left attached
 *	to each word (&lt;w&gt;) element.   Default is /noid which removes the
 *	xml:id attribute and value.</td>
 *	</tr>
 *	<tr>
 *	<td>/trim or /notrim</td>
 *	<td>Optional parameter indicating whether whitespace
 *	should be trimmed from the start and end of each XML text line.
 *	Default is /notrim, which leaves the original whitespace intact.</td>
 *	</tr>
 *	</table>
 *
 *	<p>
 *	The derived adorned output file "output.xml"  has all attributes
 *	stripped from each &lt;w&gt; tag.
 *	</p>
 *	<p>
 *	The attribute values for each "&lt;w&gt;" element in the input.xml file
 *	are extracted and output to the tab-separated values output.tab file.
 *	The order of the attribute lines matches the order of appearance
 *	of the &lt;w&gt; elements in the XML output file.   When /id is used, the
 *	xml:id value in each &lt;w&gt; element in output.xml can be matched
 *	with the corresponding xml:id value in output.tab .
 *	</p>
 *
 *	<p>
 *	The first line in output.tab contains the attribute names for
 *	each column.  Each subsequent line in the output.tab file contains
 *	at least the following information corresponding to a single word
 *	"&lt;w&gt;" element.  Some adorned files may add extra
 *	word attributes,  resulting in more columns.
 *	</p>
 *
 *	<ol>
 *	<li>xml:id -- the permanent word ID.</li>
 *	<li>eos -- the end of sentence flag (1 if word ends a sentence, 0 otherwise)
 *		</li>
 *	<li>lem -- the lemma.</li>
 *	<li>ord -- the word ordinal within the text (starts at 1)</li>
 *	<li>part --  the word part flag.  "N" for a word which is not split;
 *		"I" for the first part of a split word; "M" for the middle
 *		parts of a split word; and "F" for the final part of a split word.
 *		</li>
 *	<li>pos -- the part of speech.</li>
 *	<li>reg -- the standard spelling.</li>
 *	<li>spe -- the corrected original spelling.</li>
 *	<li>tok -- The original token.</li>
 *	</ol>
 */

public class StripWordAttributes
{
	/** Line separator. */

	protected static final String LINE_SEPARATOR =
		System.getProperty( "line.separator" );

	/**	Attributes to omit from output attributes file. */

	protected static Set<String> attrsToOmit	= new HashSet<String>();

	/**	Standard entities generated by MorphAdorner. */

	protected static Map<String, String> entitiesMap	=
		new HashMap<String, String>();

	/**	Entities pattern. */

	protected static Pattern entitiesPattern;

	/**	Entities pattern matcher. */

	protected static Matcher entitiesMatcher;

	/**	Static initializer. */

	static
	{
								//	Attributes to omit.

		attrsToOmit.add( WordAttributeNames.id );

								//	Standard entity names.

		entitiesMap.put( "quot" , "\"" );
		entitiesMap.put( "apos" , "'" );
		entitiesMap.put( "amp" , "&" );
		entitiesMap.put( "lt" , "<" );
		entitiesMap.put( "gt" , ">" );

								//	Entities pattern matcher.
		entitiesPattern	=
			Pattern.compile
			(
				"(&)(quot|apos|amp|lt|gt|#[0-9]+|#x[0-9]+)(;)"
			);
								//	Create entities matcher.

		entitiesMatcher	= entitiesPattern.matcher( "" );
	}

	/**	Main program. */

	public static void main( String[] args )
	{
								//	Must have input file name and
								//	output file names to perform conversion.
								//	Display program usage if three
								//	arguments are not provided.

		if ( args.length >= 3 )
		{
			boolean trimWhitespace	= false;
			boolean leaveID			= false;

			for ( int i = 3 ; i < args.length ; i++ )
			{
				if ( args[ i ].equals( "/id" ) )
				{
					leaveID	= true;
				}
				else if ( args[ i ].equals( "/noid" ) )
				{
					leaveID	= false;
				}
				else if ( args[ i ].equals( "/trim" ) )
				{
					trimWhitespace	= true;
				}
				else if ( args[ i ].equals( "/notrim" ) )
				{
					trimWhitespace	= false;
				}
			}

			new StripWordAttributes
			(
				args[ 0 ] ,
				args[ 1 ] ,
				args[ 2 ] ,
				leaveID ,
				trimWhitespace
			);
		}
		else
		{
			displayUsage();
			System.exit( 1 );
		}
	}

	/**	Display program usage.
	 */

	protected static void displayUsage()
	{
		System.out.println( "Usage:" );
		System.out.println( );
		System.out.println( "java edu.northwestern.at.morphadorner." +
			"tools.stripwordattributes.StripWordAttributes " +
			"input.xml output.xml output.tab [/[no]id] [/[no]trim]" );
		System.out.println( );
		System.out.println( "input.xml -- Input MorphAdornerd xml file." );
		System.out.println( "output.xml -- Derived adorned file with " +
			"word element attributes stripped." );
		System.out.println( "output.tab -- Tab delimited file of word " +
			"element attribute values." );
		System.out.println( "/id or /noid -- Optional parameter " +
			"indicating xml:id should be left attached to each word " +
			"(<w>) element.  Default is /noid which removes the " +
			"xml:id attribute and value." );
		System.out.println( "/trim or /notrim -- Optional parameter " +
			"indicating whether whitespace should be trimmed from " +
			"the start and end of each XML text line.  " +
			"Default is /notrim, which leaves the original whitespace " +
			"intact." );
	}

	/**	Create derived adorned files with character offset attributes.
	 *
	 *	@param	inputXMLFileName	Input adorned XML file name.
	 *	@param	outputXMLFileName	Output modified XML file name.
	 *	@param	outputTabFileName	Output attribute values file name.
	 *	@param	leaveID				true to leave xml:id in output<w>
	 *								elements.
	 *	@param	trimWhitespace		true to trim whitespace from
	 *								start and end of input XML lines.
	 */

	public StripWordAttributes
	(
		String inputXMLFileName ,
		String outputXMLFileName ,
		String outputTabFileName  ,
		boolean leaveID ,
		boolean trimWhitespace
	)
	{
		try
		{

								//	Open input file.

			UnicodeReader streamReader	=
				new UnicodeReader
				(
					new FileInputStream( new File( inputXMLFileName ) ) ,
					"utf-8"
				);

			BufferedReader in	= new BufferedReader( streamReader );

								//	Open output file for adorned
								//	text with all word attributes stripped.

			FileOutputStream outputStream			=
				new FileOutputStream( outputXMLFileName , false );

			BufferedOutputStream bufferedStream	=
				new BufferedOutputStream( outputStream );

			OutputStreamWriter writer	=
				new OutputStreamWriter( bufferedStream , "utf-8" );

			PrintWriter xmlPrintWriter		= new PrintWriter( writer );

								//	Open output file for tab delimited
								//	word attributes.  One line per word.

			FileOutputStream outputStream2			=
				new FileOutputStream( outputTabFileName , false );

			BufferedOutputStream bufferedStream2	=
				new BufferedOutputStream( outputStream2 );

			OutputStreamWriter writer2	=
				new OutputStreamWriter( bufferedStream2 , "utf-8" );

			PrintWriter tabPrintWriter		= new PrintWriter( writer2 );

								//	Column titles not output yet.

			boolean first		= true;

								//	Read first line of input file.

			String line			= in.readLine();

								//	Loop over adorned input file.

			while ( line != null )
			{
								//	Trim whitespace from start and
								//	end of input XML lines if requested.

				if ( trimWhitespace )
				{
					line	= line.trim();
				}
								//	Does input line contain <w> tag?

				int wPos	= line.indexOf( "<w " );

								//	If line contains <w> tag ...

				if ( wPos >= 0 )
				{
								//	Split <w> text into  attributes
								//	and word text.

					String[] groupValues	=
						WordAttributePatterns.wReplacer.matchGroups( line );

					String[] idValues		=
						WordAttributePatterns.idReplacer.matchGroups(
							groupValues[ WordAttributePatterns.ATTRS ] );

					String id	=
						idValues[ WordAttributePatterns.IDVALUE ];

								//	Get the word text.

					String wordText	=
						groupValues[ WordAttributePatterns.WORD ];

								//	Generate <w> tag stripped of
								//	attributes.   Leave xml:id if
								//	requested.

					line		=
						groupValues[ WordAttributePatterns.LEFT ] +
						"<w" +
						( leaveID ? " " + WordAttributeNames.id +
							"=\"" + id + "\"" : "" ) +
						">" +
						groupValues[ WordAttributePatterns.WORD ] +
						"</w>" +
						groupValues[ WordAttributePatterns.RIGHT ];

								//	Create tabbed line with attribute values.
								//	The xml:id value is always the
								//	first output column.

					StringBuffer attrs	= new StringBuffer();

					Map<String, String> attrsMap	=
						getAttributes
						(
							groupValues[ WordAttributePatterns.ATTRS ] ,
							groupValues[ WordAttributePatterns.WORD ]
						);

					attrs.append(
						attrsMap.get( WordAttributeNames.id ) );

					Iterator<String> iterator	=
						attrsMap.keySet().iterator();

					while ( iterator.hasNext() )
					{
						String attr	= iterator.next();

						if ( !attrsToOmit.contains( attr  ) )
						{
							attrs.append( "\t" );
							attrs.append( attrsMap.get( attr ) );
						}
					}
								//	Output column titles if this is the
								//	first word element.

					if ( first )
					{
						StringBuffer colTitles	= new StringBuffer();

						colTitles.append( WordAttributeNames.id );

						iterator	= attrsMap.keySet().iterator();

						while ( iterator.hasNext() )
						{
							String attr	= iterator.next();

							if ( !attrsToOmit.contains( attr  ) )
							{
								colTitles.append( "\t" );
								colTitles.append( attr );
							}
						}
								//	Output column titles.

						tabPrintWriter.println( colTitles );

								//	Remember we don't have to
								//	emit the column titles again.

						first	= false;
					}
								//	Output attribute values line.

					tabPrintWriter.println( attrs );
				}
								//	Output updated adorned line.

				xmlPrintWriter.println( line );

								//	Read next input line.

				line	= in.readLine();
			}
								//	Close input file.
 			in.close();
								//	Close output files.

			xmlPrintWriter.close();
			tabPrintWriter.close();
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}

	/**	Get map of attribute values for a <w> element.
	 *
	 *	@param	attrsText	String containing attribute values in
	 *						attr="value" form as extracted from
	 *						<w> element.
	 *
	 *	@param	wordText	Word text.
	 *
	 *	@return				Map with attribute name as key and
	 *						attribute value as value.
	 */

	protected static Map<String, String> getAttributes
	(
		String attrsText ,
		String wordText
	)
	{
		Map<String, String> result	= new TreeMap<String, String>();

		StringTokenizer tokenizer	= new StringTokenizer( attrsText );

		int iPos;
		String token;
		String key;
		String value;

		while ( tokenizer.hasMoreTokens() )
		{
			token	= tokenizer.nextToken();

			iPos	= token.indexOf( "=" );

			key		= token.substring( 0 , iPos );
			value	= token.substring( iPos + 1 );

			result.put( key , cleanAttributeValue( value ) );
		}

		return fillInMissingAttributes( result , wordText );
	}

	/**	Cleans an attribute value of enclosing quotes and internal entities.
	 *
	 *	@param	attrValue	Input attribute value with possible
	 *						enclosing quotes and internal entities.
	 *
	 *	@return				Attribute value with enclosing quotes removed
	 *						and internal entities resolved to their
	 *						character equivalents.
	 */

	protected static String cleanAttributeValue( String attrValue )
	{
								//	Remove starting and ending
								//	quotes.

		if ( attrValue.length() > 0 )
		{
			if ( attrValue.charAt( 0 ) == '"' )
			{
				attrValue	= attrValue.substring( 1 );
			}
		}

		if ( attrValue.length() > 0 )
		{
			if ( attrValue.charAt( attrValue.length() - 1 ) == '"' )
			{
				attrValue	=
					attrValue.substring( 0 , attrValue.length() - 1 );
			}
		}
								//	Replace entities.  This only handles
								//	&gt;, &lt;, &quot;, &apos;, &amp;,
								//	&#nn; and &#xnn .

		if ( attrValue.indexOf( "&" ) >= 0 )
		{
			StringBuffer sb	= new StringBuffer();

			String fixedEntity;
			int charNum;
								//	Loop over string looking for
								//	entities.

			while ( entitiesMatcher.find() )
			{
								//	Pick up entity name.

				String entityName	= entitiesMatcher.group( 2 );

								//	Convert numeric entity ...

				if ( entityName.charAt( 0 ) == '#' )
				{
					entityName	= entityName.substring( 1 );

					if ( entityName.charAt( 0 ) == 'x' )
					{
						entityName	= entityName.substring( 1 );

						charNum	= Integer.parseInt( entityName , 16 );
					}
					else
					{
						charNum	= Integer.parseInt( entityName );
					}

					fixedEntity	= (char)charNum + "";
				}
								//	or pick up value for non-numeric
								//	entity.  Unrecognized entities are
								//	replaced by the empty string.
				else
				{
					fixedEntity	= entitiesMap.get( entityName );

					if ( fixedEntity == null )
					{
						fixedEntity	= "";
					}
				}

				entitiesMatcher.appendReplacement( sb , fixedEntity );
			}

	 		entitiesMatcher.appendTail( sb );

 			attrValue	= sb.toString();
		}
			                    //	Return cleaned attribute value.
		return attrValue;
	}

	/**	Fill in missing attribute values.
	 *
	 *	@param	attrMap		Map with attribute name -> attribute value
	 *						entries.
	 *
	 *	@param	wordText	Word text.
	 *
	 *	@return				Updated map with missing/default entries
	 *						filled in.
	 */

	protected static Map<String, String> fillInMissingAttributes
	(
		Map<String, String> attrMap ,
		String wordText
	)
	{
		setMissingValue(
			attrMap , WordAttributeNames.tok , wordText );

		setMissingValue(
			attrMap ,
			WordAttributeNames.spe ,
			attrMap.get( WordAttributeNames.tok ) );

		setMissingValue(
			attrMap ,
			WordAttributeNames.reg ,
			attrMap.get( WordAttributeNames.spe ) );

		setMissingValue(
			attrMap ,
			WordAttributeNames.pos ,
			attrMap.get( WordAttributeNames.tok ) );

		setMissingValue(
			attrMap ,
			WordAttributeNames.lem ,
			attrMap.get( WordAttributeNames.spe ) );

		setMissingValue(
			attrMap ,
			WordAttributeNames.eos ,
			"0" );

		setMissingValue(
			attrMap ,
			WordAttributeNames.part ,
			"N" );

		return attrMap;
	}

	/**	Set missing attribute value.
	 *
	 *	@param	attrMap				Map with attribute name ->
	 *								attribute value entries.
	 *
	 *	@param	attrName			Attribute name.
	 *
	 *	@param	defaultAttrValue	Default attribute value for
	 *								attribute attrName if not present
	 *								in attrMap.
	 */

	protected static void setMissingValue
	(
		Map<String, String> attrMap ,
		String attrName ,
		String defaultAttrValue
	)
	{
		String attrValue	= attrMap.get( attrName );

		if ( attrValue == null )
		{
			attrMap.put( attrName , defaultAttrValue );
		}
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



