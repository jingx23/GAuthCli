/*Copyright 2011 Jan Scheithauer

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package de.jingx.net.main;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

import de.jingx.net.main.Base32String.DecodingException;

public class Main {
	
	private static final String CLI_SECRET = "s";
	private static final String CLI_PASSCODE = "p";

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) {
		CommandLineParser parser = new PosixParser();
		Options options = new Options();
		options.addOption(CLI_SECRET, true, "generate secret key (input is the configuration key from google)");
		options.addOption(CLI_PASSCODE, true, "generate passcode (input is the secret key)");
		options.addOption(new Option( "help", "print this message"));
		try{
			CommandLine line = parser.parse( options, args );
			if(line.hasOption(CLI_SECRET)){
				String confKey = line.getOptionValue(CLI_SECRET);
				String secret  = generateSecret(confKey);
				System.out.println("Your secret to generate pins: " + secret);
			}else if(line.hasOption(CLI_PASSCODE)){
				String secret = line.getOptionValue(CLI_PASSCODE);
				String pin    = computePin(secret, null);
				System.out.println(pin);
			}else{
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp( "GAuthCli", options );
			}
			System.out.println("Press any key to exit");
			System.in.read();
		}catch (Exception e) {
			System.out.println( "Unexpected exception:" + e.getMessage() );
		}		
		System.exit(0);
	}
	
	public static String computePin(String secret, Long counter) {
		if (secret == null || secret.length() == 0) {
			return "Null or empty secret";
		}
		try {
			final byte[] keyBytes = Base32String.decode(secret);
			Mac mac = Mac.getInstance("HMACSHA1");
			mac.init(new SecretKeySpec(keyBytes, ""));
			PasscodeGenerator pcg = new PasscodeGenerator(mac);
			if (counter == null) { // time-based totp
				return pcg.generateTimeoutCode();
			} else { // counter-based hotp
				return pcg.generateResponseCode(counter);
			}
		} catch (GeneralSecurityException e) {
			return "General security exception";
		} catch (DecodingException e) {
			return "Decoding exception";
		}
	}
	
	private static String generateSecret(String key){
		String s = key;
		s = s.replaceAll(" ", "");
		s = s.replaceAll("1", "I");
		s = s.replaceAll("0", "O");
		return s;
	}

}
