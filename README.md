pricing-tool
============

Automatically extract data of interest from a table sent via e-mail, to create a csv file.

This Java library will connect to a mail server via SMTP to download email messages and then parse an html table in the body of the message, or an attached csv file, or Excel spreadsheet of each message based on a specified parser class. The output will be a csv file containing only the data of interest as indicated in the parser class. You will need to construct your own parser class for each incoming email address/data source. 

We have provided an example general configuration (config.properties), parser configuration (parsers_config.properties), and parser class (MySampleParser.java) for you to use when building your project. Our example parses incoming mail for network-specific pricing data.

Requirements:
		JDK 1.7
		Ant 

How to build the "pricing project":
		Run "ant" from the command line
		

How to Run  the "pricing project":
		2 available modes "check mail" (option --checkmail) or "send mail" (option --sendmail) 

		Run "PricingProject" using checkmail ("--checkmail") or sendmail ("--sendmail") commands from command line.
		Below is the example:

		java -jar dist/pricing.jar --checkmail 

Configuration of the "pricing project":

	all configuration data is stored in "config.properties" file.
	
	for checkmail:	 
		- mail.username                - mail address that we listen
		- mail.password                - password for listen mail address
		- file.path.storage            - path to folder where the result parsed files will be stored
		- error.file.path.storage      - if there was any errors during parsing, the file with error rows will be created in this folder
		- mail.reader                  - path to class that contains logic for reading mails 
		- network.validator.class.path - path to Network Validator class
		- file.networks                - .csv file that contain all the networks that are in our system for validating networks

	for sendmail:
		- email.list                   - path to .txt file that contain the email addresses of all suppliers.
		- message.template             - path to .txt file that store the template of message for sending email
		- message.subject              - subject of sending mail
		- file.to.sent                 - path to .csv file with pricing updates from you to send to your customers
		- mail.converter               - path to class that contain logic for sending mails
		

	Parsers configuration is stored in "parsers_config.properties" file. 
	You will need to associate a parser class for each email address, for example:

	abc@foobar.com=net.pricing.common.parser.impl.MyFooBarParser
	xyz@acme=net.pricing.common.parser.impl.MyAcmeParser

	By default, only incoming updates are taken into account, if you want to take all pricing changes you will need to add the "all" option:

	xyz@acme=net.pricing.common.parser.impl.MyAcmeParser;all




Pricing Exchange Management
