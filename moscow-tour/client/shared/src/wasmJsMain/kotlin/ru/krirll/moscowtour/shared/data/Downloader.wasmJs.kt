package ru.krirll.moscowtour.shared.data

import io.ktor.util.toJsArray
import org.khronos.webgl.Int8Array

@JsFun("""
    function saveFileFromBytes(bytes, fileName) {
        var uint8Array = Uint8Array.from(bytes);
        var blob = new Blob([uint8Array], { type: 'application/octet-stream' });
        var url = URL.createObjectURL(blob);

        var a = document.createElement('a');
        a.href = url;
        a.download = fileName;
        a.style.display = 'none';
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);

        URL.revokeObjectURL(url);
    }
""")
private external fun saveFileFromBytes(bytes: Int8Array, fileName: String)

actual suspend fun saveFileFromResponse(byteArray: ByteArray, fileName: String) {
    saveFileFromBytes(byteArray.toJsArray(), fileName)
}
