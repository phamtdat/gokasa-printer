# GOKASA Android Printer  
  
## Requirements  
  
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
  
### Example of web call in HTML & JS:  
```html
<!-- simple intent -->
<a href="ethereal://gokasa-print?data=hello%20world" target="_blank">Print intent</a>
```
  
```js
const data = encodeURIComponent("hello world!");
window.open(`ethereal://gokasa-print?data=${data}`);
```
  
### Test web interface:  
[https://dev.gokasa.cz](https://dev.gokasa.cz)  

### Deprecated
```html
<!-- legacy intent -->
<a href="intent://gokasa-print#Intent;scheme=scheme;package=package;S.browser_fallback_url=https%3A%2F%2Fdev.gokasa.cz%2Finstall-gokasa-printer.html;S.data=hello%20world;end;" target="_blank">Print intent</a>
```

```js
const data = encodeURIComponent("hello world!");
const scheme = "scheme";
const package = "package";
const fallbackURL = encodeURIComponent("https://dev.gokasa.cz/install-gokasa-printer.html");
window.open(
    "intent://gokasa-print#Intent;" +
    "scheme=" + scheme + ";" +
    "package=" + package + ";" +
    "S.browser_fallback_url=" + fallbackURL + ";" +
    "S.data=" + data + ";" +
    "end;"
);
```
  
