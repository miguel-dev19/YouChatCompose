package cu.alexgi.youchat.core.util

import android.graphics.Bitmap
import android.graphics.Color
import kotlin.math.*

object BlurHashDecoder {
    fun decode(blurHash: String, width: Int, height: Int, punch: Float = 1f): Bitmap? {
        if (blurHash.length < 6) return null
        val sizeFlag = decode83(blurHash[0])
        val numY = (sizeFlag / 9) + 1
        val numX = (sizeFlag % 9) + 1
        val quantisedMaxValue = decode83(blurHash[1])
        val maxValue = (quantisedMaxValue + 1) / 166f
        if (blurHash.length != 4 + 2 * numX * numY) return null
        val colors = Array(numX * numY) { FloatArray(3) }
        for (i in 0 until numX * numY) {
            if (i == 0) { val value = decode83(blurHash.substring(2, 6)); decodeDC(value, colors[i], maxValue * punch) }
            else { val value = decode83(blurHash.substring(4 + i * 2, 6 + i * 2)); decodeAC(value, colors[i], maxValue * punch) }
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
    private fun decodeDC(v:Int,c:FloatArray,m:Float){c[0]=srgbToLinear((v shr 16)and 255);c[1]=srgbToLinear((v shr 8)and 255);c[2]=srgbToLinear(v and 255)}
    private fun decodeAC(v:Int,c:FloatArray,m:Float){val r=(v/(19*19));val g=(v/19)%19;val b=v%19;c[0]=signPow((r-9)/9f,2f)*m;c[1]=signPow((g-9)/9f,2f)*m;c[2]=signPow((b-9)/9f,2f)*m}
    private fun decode83(s:String):Int{var v=0;for(c in s)v=v*83+charToInt[c]!!;return v}
    private fun decode83(c:Char):Int=charToInt[c]?:0
    private fun signPow(v:Float,e:Float):Float=sign(v)*abs(v).pow(e)
    private fun srgbToLinear(v:Int):Float{val f=v/255f;return if(f<=0.04045f)f/12.92f else ((f+0.055f)/1.055f).pow(2.4f)}
    private fun linearToSrgb(v:Float):Int{val c=v.coerceIn(0f,1f);val s=if(c<=0.0031308f)c*12.92f else 1.055f*c.pow(1/2.4f)-0.055f;return(s*255).toInt().coerceIn(0,255)}
    private val charToInt=mapOf('0'to 0,'1'to 1,'2'to 2,'3'to 3,'4'to 4,'5'to 5,'6'to 6,'7'to 7,'8'to 8,'9'to 9,'A'to 10,'B'to 11,'C'to 12,'D'to 13,'E'to 14,'F'to 15,'G'to 16,'H'to 17,'I'to 18,'J'to 19,'K'to 20,'L'to 21,'M'to 22,'N'to 23,'O'to 24,'P'to 25,'Q'to 26,'R'to 27,'S'to 28,'T'to 29,'U'to 30,'V'to 31,'W'to 32,'X'to 33,'Y'to 34,'Z'to 35,'a'to 36,'b'to 37,'c'to 38,'d'to 39,'e'to 40,'f'to 41,'g'to 42,'h'to 43,'i'to 44,'j'to 45,'k'to 46,'l'to 47,'m'to 48,'n'to 49,'o'to 50,'p'to 51,'q'to 52,'r'to 53,'s'to 54,'t'to 55,'u'to 56,'v'to 57,'w'to 58,'x'to 59,'y'to 60,'z'to 61,'#'to 62,'$'to 63,'%'to 64,'*'to 65,'+'to 66,','to 67,'-'to 68,'.'to 69,'/'to 70,':'to 71,';'to 72,'='to 73,'?'to 74,'@'to 75,'['to 76,']'to 77,'^'to 78,'_'to 79,'{'to 80,'|'to 81,'}'to 82,'~'to 83)
}
