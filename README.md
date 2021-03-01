# Base64Image

[![](https://jitpack.io/v/pixelcarrot/base64image.svg)](https://jitpack.io/#pixelcarrot/base64image)

## Example

### Bitmap to Base64 String
```kotlin
Base64Image.encode(bitmap) { base64 ->
    base64?.let {
        // success
    }
}
```

### Base64 String to Bitmap
```kotlin
Base64Image.decode(base64, { bitmap ->
    bitmap?.let {
        // success
    }
})
```

## Installation

```groovy
repositories {
    maven { url "https://jitpack.io" }
}
```

```groovy
dependencies {
    implementation 'com.github.pixelcarrot:base64image:1.0.1'
}
```

## Author

Justin Nguyen, nguyen.cocoa@gmail.com

## License

Base64Image is available under the MIT license. See the LICENSE file for more info.