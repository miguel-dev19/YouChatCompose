package cu.alexgi.youchat.core.util

import android.graphics.Bitmap
import kotlin.math.*

object BlurHashEncoder {
    fun encode(bitmap: Bitmap, componentX: Int = 4, componentY: Int = 3): String? {
        val width=bitmap.width;val height=bitmap.height
        if(width<1||height<1)return null
        val scaled=Bitmap.createScaledBitmap(bitmap,32,32,true)
        val pixels=IntArray(1024);scaled.getPixels(pixels,0,32,0,0,32,32)
        val factors=Array(componentY){y->Array(componentX){x->var r=0.0;var g=0.0;var b=0.0
            for(py in 0..31){val yB=cos(PI*y*py/31)
                for(px in 0..31){val xB=cos(PI*x*px/31);val basis=yB*xB;val pixel=pixels[py*32+px]
                    r+=basis*srgbToLinear((pixel shr 16)and 0xFF);g+=basis*srgbToLinear((pixel shr 8)and 0xFF);b+=basis*srgbToLinear(pixel and 0xFF)}}
            val scale=if(x==0&&y==0)1.0 else 2.0;val n=scale/1024;Triple(r*n,g*n,b*n)}}
        val dc=factors[0][0];var maxAc=0.0
        for(y in 0 until componentY)for(x in 0 until componentX){if(x==0&&y==0)continue;val(r,g,b)=factors[y][x];maxAc=max(maxAc,abs(r));maxAc=max(maxAc,abs(g));maxAc=max(maxAc,abs(b))}
        val acScale=if(maxAc>0)18.0/maxAc else 0.0
        val sb=StringBuilder()
        val sizeFlag=(componentX-1)+(componentY-1)*9;sb.append(encode83(sizeFlag,1))
        val maxAcQ=min(82,(acScale*166).toInt().coerceIn(0,82));sb.append(encode83(maxAcQ,1))
        val dcR=linearToSrgb(dc.first).coerceIn(0,255);val dcG=linearToSrgb(dc.second).coerceIn(0,255);val dcB=linearToSrgb(dc.third).coerceIn(0,255)
        sb.append(encode83((dcR shl 16)+(dcG shl 8)+dcB,4))
        for(y in 0 until componentY)for(x in 0 until componentX){if(x==0&&y==0)continue;val(r,g,b)=factors[y][x]
            val qr=(signPow(r*acScale,0.5)*9+9.5).toInt().coerceIn(0,18);val qg=(signPow(g*acScale,0.5)*9+9.5).toInt().coerceIn(0,18);val qb=(signPow(b*acScale,0.5)*9+9.5).toInt().coerceIn(0,18)
            sb.append(encode83(qr*19*19+qg*19+qb,1))}
        return sb.toString()
    }
    private fun srgbToLinear(v:Int):Double{val f=v/255.0;return if(f<=0.04045)f/12.92 else ((f+0.055)/1.055).pow(2.4)}
    private fun linearToSrgb(v:Double):Int{val c=v.coerceIn(0.0,1.0);val s=if(c<=0.0031308)c*12.92 else 1.055*c.pow(1/2.4)-0.055;return(s*255).toInt().coerceIn(0,255)}
    private fun signPow(v:Double,e:Double):Double=sign(v)*abs(v).pow(e)
    private fun encode83(v:Int,l:Int):String{var x=v;val r=CharArray(l);for(i in l-1 downTo 0){r[i]=intToChar[x%83];x/=83};return String(r)}
    private val intToChar=charArrayOf('0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','#','$','%','*','+',',','-','.','/',':',';','=','?','@','[',']','^','_','{','|','}','~')
}
