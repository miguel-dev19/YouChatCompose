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
        val maxValue = (decode83(blurHash[1]) + 1) / 166f
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
    private fun decodeDC(v:Int,c:FloatArray,m:Float){c[0]=srgbToLinear((v shr 16)and 255);c[1]=srgbToLinear((v shr 8)and 255);c[2]=srgbToLinear(v and 255)}
    private fun decodeAC(v:Int,c:FloatArray,m:Float){val r=(v/(19*19));val g=(v/19)%19;val b=v%19;c[0]=signPow((r-9)/9f,2f)*m;c[1]=signPow((g-9)/9f,2f)*m;c[2]=signPow((b-9)/9f,2f)*m}
    private fun decode83(s:String):Int{var v=0;for(c in s)v=v*83+charToInt(c);return v}
    private fun decode83(c:Char):Int=charToInt(c)
    private fun signPow(v:Float,e:Float):Float=sign(v)*abs(v).pow(e)
    private fun srgbToLinear(v:Int):Float{val f=v/255f;return if(f<=0.04045f)f/12.92f else ((f+0.055f)/1.055f).pow(2.4f)}
    private fun linearToSrgb(v:Float):Int{val c=v.coerceIn(0f,1f);val s=if(c<=0.0031308f)c*12.92f else 1.055f*c.pow(1/2.4f)-0.055f;return(s*255).toInt().coerceIn(0,255)}
    private fun charToInt(c:Char):Int = when(c){
        '0'->0,'1'->1,'2'->2,'3'->3,'4'->4,'5'->5,'6'->6,'7'->7,'8'->8,'9'->9,
        'A'->10,'B'->11,'C'->12,'D'->13,'E'->14,'F'->15,'G'->16,'H'->17,'I'->18,'J'->19,'K'->20,'L'->21,'M'->22,'N'->23,'O'->24,'P'->25,'Q'->26,'R'->27,'S'->28,'T'->29,'U'->30,'V'->31,'W'->32,'X'->33,'Y'->34,'Z'->35,
        'a'->36,'b'->37,'c'->38,'d'->39,'e'->40,'f'->41,'g'->42,'h'->43,'i'->44,'j'->45,'k'->46,'l'->47,'m'->48,'n'->49,'o'->50,'p'->51,'q'->52,'r'->53,'s'->54,'t'->55,'u'->56,'v'->57,'w'->58,'x'->59,'y'->60,'z'->61,
        '#'->62,'$'->63,'%'->64,'*'->65,'+'->66,','->67,'-'->68,'.'->69,'/'->70,':'->71,';'->72,'='->73,'?'->74,'@'->75,'['->76,']'->77,'^'->78,'_'->79,'{'->80,'|'->81,'}'->82,'~'->83
        else -> 0
    }
}
