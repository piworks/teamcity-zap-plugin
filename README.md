= Zap-plugin

Zap Attack Proxy Automation Plugin

Purpose of this plugin is to add ZAP scan into Continuous Integration.

= Installation

OWASP ZAP needs to be installed.
Python 3.7+ is required.
Agent\xsl_to_html.py needs to be and extracted to Agent\work\{work.id}.

= Configuration

ZAP path must be specified in the build step.
 
= How To Use

Simply enter URL you want to scan and your ZAP path in the Zap Security build step.
Once the run is complete you can view your report at ZAP Security Report section inside the build logs.

= Future Plans

Removing xsl_to_html.py and python dependencies.
