# Base64Image

## Example

```java
Base64Image.with(this)
        .encode(bitmap)
        .into(new RequestEncode.Encode() {
            @Override
            public void onSuccess(String base64) {
            
            }
            
            @Override
            public void onFailure() {

            }
        });
```


```java
Base64Image.with(this)
        .decode("iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAABHNCSVQICAgIfAhkiAAAAA1JREFU CB1jYGD4/x8AAwIB/6fhVKUAAAAASUVORK5CYII=")
        .into(new RequestDecode.Decode() {
            @Override
            public void onSuccess(Bitmap bitmap) {
            
            }
            
            @Override
            public void onFailure() {
            
            }
        });
```

## Installation

```javascript
repositories {
    maven { url "https://jitpack.io" }
}
```

```javascript
dependencies {
    compile 'com.github.nekoloop:Base64Image:0.1.0'
}
```

## Author

Nguyen Hoang Anh Nguyen, nguyen.cocoa@gmail.com

## License

Base64Image is available under the MIT license. See the LICENSE file for more info.