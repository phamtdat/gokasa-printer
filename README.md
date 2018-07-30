# Gokasa Printer requirements

### Description:
- A mobile app for Android that captures intent calls from a web browser. These intents contain URL encoded data. The mobile app will convert this data back and send it to the printer via bluetooth/USB and the printer will print it seamlessly.
- The app should include an interface to allow users to set up and connect to the printer.

### Supported printer models:
- Elio P200 (XPrinter): [Materials & Docs](https://drive.google.com/drive/folders/0B52TYGb_gnqpU1JoQWp3SUVEUnc)
- Cashino PTP-II (Cashino): [Materials & Docs](https://drive.google.com/drive/folders/0B52TYGb_gnqpdkY1Y2J3MkdwNWs)
    
### Web browser:
- Google Chrome for Android
    
### Target devices:
- Tablets and phones with Android 5 and later
    
### Example of web call in JS: 
`
<a href="intent://gokasa-print#Intent;scheme=scheme;package=packase;S.browser_fallback_url=https%3A%2F%2Fdev.gokasa.cz%2Finstall-gokasa-printer.html;S.data=hello%20world" target="_blank"/>;
`
    
### Test web interface: 
[https://dev.gokasa.cz](https://dev.gokasa.cz)
