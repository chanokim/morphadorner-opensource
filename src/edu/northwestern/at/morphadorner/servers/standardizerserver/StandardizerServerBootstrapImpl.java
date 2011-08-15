package edu.northwestern.at.morphadorner.servers.standardizerserver;

/*	Please see the license information at the end of this file. */

import java.rmi.*;
import java.rmi.server.*;
import java.net.*;

/**	Server bootstrap remote object implementation.
 */

public class StandardizerServerBootstrapImpl
	extends UnicastRemoteObject
	implements StandardizerServerBootstrap
{
	/**	Creates a new BootstrapImpl object.
	 *
	 *	@throws	RemoteException
	 */

	StandardizerServerBootstrapImpl()
		throws RemoteException
	{
		super( StandardizerServerConfig.getRmiPort() );
	}

	/**	Starts a new session.
	 *
	 *	@return			A session object.
	 *
	 *	@throws	RemoteException
	 */

	public StandardizerServerSession startSession()
		throws RemoteException
	{
		return new StandardizerServerSessionImpl();
	}

	/**	Shuts down the server.
	 *
	 *  @param	uri		The URI for the server object.
	 *
	 *	<p>Shutdown requests must originate from the local host, or
	 *	they are ignored.
	 *
	 *	@throws	Exception
	 */

	public void shutdown ( String uri )
		throws Exception
	{
		String myAddress = InetAddress.getLocalHost().getHostAddress();
		String hisAddress = UnicastRemoteObject.getClientHost();
		if (!myAddress.equals(hisAddress)) return;
		Naming.unbind(uri);
		new Thread (
			new Runnable () {
				public void run () {
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
					}
					StandardizerServerLogger.terminate();
					System.exit(0);
				}
			}
		).start();
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



