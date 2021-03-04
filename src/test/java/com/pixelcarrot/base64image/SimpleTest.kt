import android.graphics.Bitmap
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.mock
import com.pixelcarrot.base64image.Base64Image
import org.junit.Test
import org.mockito.Mockito.verify

class SimpleTest {
    @Test
    fun `encode a base64 string should complete`() {
        val callback = mock<(String?) -> Unit>()
        Base64Image.encode(any(), callback)
        verify(callback).invoke(anyOrNull())
    }

    @Test
    fun `decode a bitmap should complete`() {
        val callback = mock<(Bitmap?) -> Unit>()
        Base64Image.decode(any(), callback)
        verify(callback).invoke(anyOrNull())
    }
}