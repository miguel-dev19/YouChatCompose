package cu.alexgi.youchat.core.util

import android.graphics.Bitmap
import android.graphics.Color
import kotlin.math.*

object BlurHashDecoder {
    private val charToInt = IntArray(128).also { arr ->
        "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz#$%*+,-./:;=?@[]^_{|}~".forEachIndexed { i, c -> arr[c.code] = i }
    }
    
    fun decode(blurHash: String, width: Int, height: Int, punch: Float = 1f): Bitmap? {
        if (blurHash.length < 6) return null
        val sizeFlag = decode83(blurHash[0].code)
        val numY = (sizeFlag / 9) + 1
        val numX = (sizeFlag % 9) + 1
        val maxValue = (decode83(blurHash[1].code) + 1) / 166f
        if (blurHash.length != 4 + 2 * numX * numY) return null
        val colors = Array(numX * numY) { FloatArray(3) }
        for (i in 0 until numX * numY) {
            if (i == 0) { val v = decode83(blurHash.substring(2, 6)); decodeDC(v, colors[i], maxValue * punch) }
            else { val v = decode83(blurHash.substring(4 + i * 2, 6 + i * 2)); decodeAC(v, colors[i], maxValue * punch) }
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        for (y in 0 until height) for (x in 0 until width) {
            var r=0f;var g=0f;var b=0f
            for (j in 0 until numY) for (i in 0 until numX) {
                val basis = cos(PI.toFloat()*x*i/width)*cos(PI.toFloat()*y*j/height)
                val c=colors[j*numX+i];r+=c[0]*basis;g+=c[1]*basis;b+=c[2]*basis
            }
            bitmap.setPixel(x,y,Color.rgb(linearToSrgb(r),linearToSrgb(g),linearToSrgb(b)))
        }
        return bitmap
    }
    
    private fun decode83(code: Int): Int = if (code in 0..127) charToInt[code] else 0
    private fun decode83(s: String): Int { var v=0; for(c in s) v=v*83+decode83(c.code); return v }
    private fun decodeDC(v:Int,c:FloatArray,m:Float){c[0]=srgbToLinear((v shr 16)and 255);c[1]=srgbToLinear((v shr 8)and 255);c[2]=srgbToLinear(v and 255)}
    private fun decodeAC(v:Int,c:FloatArray,m:Float){val r=(v/(19*19));val g=(v/19)%19;val b=v%19;c[0]=signPow((r-9)/9f,2f)*m;c[1]=signPow((g-9)/9f,2f)*m;c[2]=signPow((b-9)/9f,2f)*m}
    private fun signPow(v:Float,e:Float):Float=sign(v)*abs(v).pow(e)
    private fun srgbToLinear(v:Int):Float{val f=v/255f;return if(f<=0.04045f)f/12.92f else ((f+0.055f)/1.055f).pow(2.4f)}
    private fun linearToSrgb(v:Float):Int{val c=v.coerceIn(0f,1f);val s=if(c<=0.0031308f)c*12.92f else 1.055f*c.pow(1/2.4f)-0.055f;return(s*255).toInt().coerceIn(0,255)}
}
