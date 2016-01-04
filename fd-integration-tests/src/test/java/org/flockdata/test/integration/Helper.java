/*
 * Copyright (c) 2012-2014 "FlockData LLC"
 *
 * This file is part of FlockData.
 *
 * FlockData is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FlockData is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with FlockData.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.flockdata.test.integration;

import org.apache.commons.codec.binary.Base64;
import org.flockdata.model.Company;
import org.flockdata.model.Entity;
import org.flockdata.model.EntityLog;
import org.flockdata.model.Fortress;
import org.flockdata.track.bean.ContentInputBean;
import org.flockdata.track.bean.EntityInputBean;
import org.flockdata.track.service.EntityService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * User: mike
 * Date: 16/09/14
 * Time: 4:18 PM
 */
public class Helper {
    static Authentication AUTH_MIKE = new UsernamePasswordAuthenticationToken("mike", "123");
    private static final Logger logger = LoggerFactory.getLogger(Helper.class);


    //EntityInputBean beanA = new EntityInputBean(fortress.getName(), "olivia@sunnybell.com", "DocType", DateTime.now(), "AAA");
    public static EntityInputBean getEntity(Fortress fortress, String user, String docType, String code, ContentInputBean blah) {
        return new EntityInputBean(fortress.getName()
                , user
                , docType
                , DateTime.now()
                , code)
                .setContent(blah);
    }
    public static String getPdfDoc() {
        // Romeo is a keyword
        // The quick brown fox jumps over the lazy dog
        return "JVBERi0xLjMKJcTl8uXrp/Og0MTGCjQgMCBvYmoKPDwgL0xlbmd0aCA1IDAgUiAv\n" +
                "RmlsdGVyIC9GbGF0ZURlY29kZSA+PgpzdHJlYW0KeAF9U01TgzAQvedXrDc4mJKU\n" +
                "ADn6efBkZzLjwfGg2ForVCHWrx/rbzGE7ILttMOBl92X3WXfo4EZNJC4R2kFRSp4\n" +
                "oaGdww2sYXJmBZQWhH9s6UiBUI/ZlT8cJ1wkWhcqZZUjDkeoYAkL30ZNuU6llqAy\n" +
                "nukOpEXOpy4G+VRwqRz435t1kyXgejduiA6K4XKepzyXmYayhlMDIhD6l6lhYkw3\n" +
                "ulnALUQmZgmXEC3jbjYH5gggBh9oMLBB8IygfEHSQx9iUfuKyU8EawRIXoTKRP3C\n" +
                "zAqpm5j149RvGLJIomsfNGuLufdQevtzWISMCuvdI/j5xtwjhqjFU4jcgbnqFidB\n" +
                "MLe46CgGs4IL4/RzdpCdHfbKkYmxHFJxoVVSeNXGB9ZL4zp00mx18IY7KHpajLqw\n" +
                "w6KfxKCcR4CWYmvaBu0ngGFzlnQfRCE2SYG7vD7HZV4GTSBY7Xf/pR35aCzMYHk7\n" +
                "R4e0gw3IImQa8mxLIdLWlQiex8koRZalUckjO+bd8h4b/iCLQ9PiqK7zlbeUd9Ds\n" +
                "D9hq5EgKZW5kc3RyZWFtCmVuZG9iago1IDAgb2JqCjQwNwplbmRvYmoKMiAwIG9i\n" +
                "ago8PCAvVHlwZSAvUGFnZSAvUGFyZW50IDMgMCBSIC9SZXNvdXJjZXMgNiAwIFIg\n" +
                "L0NvbnRlbnRzIDQgMCBSIC9NZWRpYUJveCBbMCAwIDU5NSA4NDJdCj4+CmVuZG9i\n" +
                "ago2IDAgb2JqCjw8IC9Qcm9jU2V0IFsgL1BERiAvVGV4dCBdIC9Db2xvclNwYWNl\n" +
                "IDw8IC9DczIgMTAgMCBSIC9DczEgNyAwIFIgPj4gL0ZvbnQKPDwgL1RUMSA4IDAg\n" +
                "UiAvVFQyIDkgMCBSID4+ID4+CmVuZG9iagoxMSAwIG9iago8PCAvTGVuZ3RoIDEy\n" +
                "IDAgUiAvTiAxIC9BbHRlcm5hdGUgL0RldmljZUdyYXkgL0ZpbHRlciAvRmxhdGVE\n" +
                "ZWNvZGUgPj4Kc3RyZWFtCngBpVcHVFPJGp5bkkAgoVcpoSPNgHSkREoIIL0INmIS\n" +
                "SCCEGBIUxIYuruDaxYJlRRdFXWwrAmvFgoXFimUtD3RRUNbFgg1X39wENe6+s++d\n" +
                "83LPcL/555+/z38HAPQsuFKpGAUAFEjkMnYyK3N8ZhaDcgdoABOgAzyADpdXJGUl\n" +
                "JsZBFiAplAiI99e/V9cBQlCuuhOyvl77rzMSX1DEg1zH4SjhF/EKAEDGAEDZwJPK\n" +
                "5ABo3IR0u+lyKYHfQ2yYn5ocDoCmLpxrD++FZGDNFkgEMhGPwZZxSxhsbkEBl+Hl\n" +
                "4cVIlBXmiMT/wWpi0//zKxArCLuJnxUc2kX5KbHw7QHtnyGQpKVADP1A6njcSAL7\n" +
                "Q3xfKh+bDHEMAKi5Ij+NBbEbxPwcWVQaxCEQLxMqognsDXFHqTA1A2IoH0MlU+MT\n" +
                "IA6EmJFfGEvIsYU4i1cUngWxM8RlQgGHyJM9xNtkhckEvwvEZ/mCiEiI4yF+LpJz\n" +
                "UlUYDysqTiHo0DZ8SakwHK4TuvAzedyYRIgdIf5NIGYTuqAcElUqTyRkwjnJWyKO\n" +
                "J3SFQVwhKFL6COekM3JhajSkewFA1pfLUom90B5yRI4oigNxFMRCoSyaoEN/yQ1S\n" +
                "sbK2YEzI/TJFMuE79JHiy5VFsiGG8aFI+dwIIrZKO0E6wgUCUAimwr88IAHdgAGK\n" +
                "gAgUK1Eu4IICOBjQAjc42JBLAocMcvAglQ0RF5QoEcH5idcD8jJAIlwtBDmQVwz3\n" +
                "sJWyVBx8OGeAXKWcIshTpJwRsnuHZat0EnISlfpUUgiNBFffMFch3MdX2qeSTuxS\n" +
                "yQ6HWiWgFK4RspXW4yY4Ex8NRxAehwfjTEj/ZL+7kj5GSfOCVUdo/WI7YWnfZ63T\n" +
                "gOIr64lVdd+JOJ2AHsshVQyjKBn2rwha8x5KzR/erb4HalxmrnCWSquXJHEm1ykt\n" +
                "VsawXDZFxLu4dGDYb1Xki6AMVfR6lG91SYR2dyD9Yn1b2WFTwLi++Pg5wNit23z2\n" +
                "i+xOvGniVe22MpCgnn1lPfD/ln1oDekK6RLpHukaYMD3b6ROUi9Et0l34HPrs9Qv\n" +
                "eVBll7BTrWYQTMlJWMmC0RErVwsgB5EtgTJXqpwoIJbDSOcod7v/JR/qtaSqTPV8\n" +
                "EVWh0p4L5apmajYoKUSVEPqJivl7/tTr/Otoq7Sp71GLdY5kmblUOrmubEgg/RwR\n" +
                "BpE/wcL4V/Gg3I25l9nP3MbczXzBvPeFg3mD+Tuzk7kFrjzBVmIHsSNYM9aCtQMG\n" +
                "nLVgJ7BmJdqNHYbPvs/7vj4Zqjh/fTKIU8YbPglEBOTDtah+Zj6d2U85IeQQGSH4\n" +
                "P8Uxb/iE/lOlEfH83yxSj/fXnUBVAcoY0+3onnQK3YXuQ2fREboNfLzoYRDZ0W3p\n" +
                "cXQTuBpNd6JH0Ed8jsenrIkhhagikfJN2Pylg2VCKz9VG+GfEK7JlBzcYX//6iPj\n" +
                "q/NE9DKR+mlEaPA0qjT9Uy9UqxKQBjWJwHRohwzGlegSEmUPVechujDRvWAXQiYo\n" +
                "c6hec586mhXuiXNgR0sADJyF++Bhw5jocmPgQ/Q4VcW646FwNQSPwP2J/kcOJDuR\n" +
                "I8lOynVlpyFHkKPJUYBB9iTo5FHkGIgDCC65YAa8LwAQXigtkYlyhXIGC95MBAyO\n" +
                "hOfhxvBiesKvE3HPIXgAeJGkvL8gxu08haxYRcOJFwlQ4R3IEJjBr6Id/MK6Q8v8\n" +
                "QBD85kXCb3gCSAWZYDL0VAizIYPRKQPzQAWoAsvAarAebAbbQD1oAPvBIXAY9tcz\n" +
                "4AK4BDrBbdAFesATMABegSEEQSgIDTFAzBBrxAFxRbwQfyQEiUTikGQkE8lGchEJ\n" +
                "okDKkPlIFbICWY9sQeqRfUgzcgI5h1xGbiHdSB/yHHmHYqg2aohaoo7oKNQfZaGx\n" +
                "aCo6Cc1Fp6Gl6AJ0CboWrUV3o43oCfQC2ol2oU/QQQxgWpgxZoO5Y/5YOJaAZWE5\n" +
                "mAybjVVi1Vgt1gDPcRt2FevC+rG3OBk3wBm4O8xENJ6G8/Bp+Gx8Mb4e34E34qfw\n" +
                "q3g3PoB/INFIFiRXUiCJQxpPyiVNJ1WQqkl1pIOk07D39pBekclkY5gfP5i3THIe\n" +
                "eSZ5MXkjeQ/5OPky+QF5kEKhmFFcKcGUBAqXIqdUUNZRdlOOUa5QeihvNLQ0rDW8\n" +
                "NKI0sjQkGuUa1Ro7NY5qXNF4pDGkqavpoBmomaDJ1yzRXKq5TbNF86Jmj+YQVY/q\n" +
                "RA2mplLzqPOoa6kN1NPUO9QXWlpatloBWklaIq25Wmu19mqd1erWequtr+2iHa49\n" +
                "UVuhvUR7u/Zx7VvaL2g0miMtjJZFk9OW0OppJ2n3aG/oBnQPOofOp8+h19Ab6Vfo\n" +
                "T3U0dRx0WDqTdUp1qnUO6FzU6dfV1HXUDdfl6s7WrdFt1r2hO6hnoOepl6BXoLdY\n" +
                "b6feOb1efYq+o36kPl9/gf5W/ZP6DwwwAzuDcAOewXyDbQanDXoMyYZOhhzDPMMq\n" +
                "wx8NOwwHjPSNvI3SjWYY1RgdMeoyxowdjTnGYuOlxvuNrxu/M7E0YZkITBaZNJhc\n" +
                "MXltOsI0zFRgWmm6x7TT9J0ZwyzSLN9sudkhs7vmuLmLeZL5dPNN5qfN+0cYjgga\n" +
                "wRtROWL/iF8tUAsXi2SLmRZbLdotBi2tLNmWUst1lict+62MrcKs8qxWWR216rM2\n" +
                "sA6xFlmvsj5m/ZhhxGAxxIy1jFOMARsLm2gbhc0Wmw6bIVsn2zTbcts9tnftqHb+\n" +
                "djl2q+xa7Qbsre3H2ZfZ77L/1UHTwd9B6LDGoc3htaOTY4bjQsdDjr1Opk4cp1Kn\n" +
                "XU53nGnOoc7TnGudr40kj/QfmT9y48hLLqiLj4vQpcbloivq6usqct3oetmN5Bbg\n" +
                "JnGrdbvhru3Oci923+Xe7WHsEedR7nHI4+ko+1FZo5aPahv1genDFMMv1G1Pfc8Y\n" +
                "z3LPFs/nXi5ePK8ar2ujaaOjRs8Z3TT6mbert8B7k/dNHwOfcT4LfVp9/vT185X5\n" +
                "Nvj2+dn7Zftt8Lvhb+if6L/Y/2wAKWBswJyAwwFvA30D5YH7A/8Icg/KD9oZ1DvG\n" +
                "aYxgzLYxD4Jtg7nBW4K7Qhgh2SHfh3SF2oRyQ2tD74fZhfHD6sIesUay8li7WU/H\n" +
                "MsfKxh4c+zo8MHxW+PEILIIdURnREakfmRa5PvJelG1UbtSuqAG2D3sm+3g0KTo2\n" +
                "enn0DY4lh8ep5wzE+MXMijkVqx2bErs+9n6cS5wsrmUcOi5m3Mpxd+Id4iXxhxJA\n" +
                "AidhZcLdRKfEaYk/J5GTEpNqkh4meyaXJbelGKRMSdmZ8ip1bOrS1NtpzmmKtNZ0\n" +
                "nfSJ6fXprzMiMlZkdI0fNX7W+AuZ5pmizKYsSlZ6Vl3W4ITICasn9Ez0mVgx8fok\n" +
                "p0kzJp2bbD5ZPPnIFJ0p3CkHsknZGdk7s99zE7i13MGpnKkbpg7wwnlreE/4YfxV\n" +
                "/D5BsGCF4FFOcM6KnN7c4NyVuX3CUGG1sF8ULlovepYXnbc573V+Qv72/I/iDPGe\n" +
                "Ao2C7IJmib4kX3Kq0KpwRuFlqau0Qto1LXDa6mkDslhZXRFSNKmoSW4I/ylsVzgr\n" +
                "vlF0F4cU1xS/mZ4+/cAMvRmSGe0lLiWLSh6VRpX+MBOfyZvZWmZTNq+sexZr1pbZ\n" +
                "yOyps1vn2M1ZMKdnLnvujnnUefnzfilnlq8ofzk/Y37LAssFcxc8+Ib9za4KeoWs\n" +
                "4sbCoIWbv8W/FX3bsWj0onWLPlTyK89XMauqq94v5i0+/53nd2u/+7gkZ0nHUt+l\n" +
                "m5aRl0mWXV8eunzHCr0VpSserBy3snEVY1Xlqperp6w+V+1dvXkNdY1iTdfauLVN\n" +
                "6+zXLVv3fr1wfWfN2Jo9Gyw2LNrweiN/45VNYZsaNlturtr87nvR9ze3sLc01jrW\n" +
                "Vm8lby3e+nBb+ra2H/x/qK8zr6uq+3O7ZHvXjuQdp+r96ut3WuxcugvdpdjVt3vi\n" +
                "7ks/RvzY1ODesGWP8Z6qvWCvYu/jfdn7ru+P3d96wP9Aw08OP204aHCwshFpLGkc\n" +
                "OCQ81NWU2XS5Oaa5tSWo5eDPHj9vP2xzuOaI0ZGlR6lHFxz9eKz02OBx6fH+E7kn\n" +
                "HrROab19cvzJa6eSTnWcjj199kzUmZNtrLZjZ4PPHj4XeK75vP/5Qxd8LzS2+7Qf\n" +
                "/MXnl4Mdvh2NF/0uNl0KuNRyeczlo1dCr5y4GnH1zDXOtQud8Z2Xr6ddv3lj4o2u\n" +
                "m/ybvbfEt579Wvzr0O258HpeeVf3bvU9i3u1/xr5rz1dvl1HuiO62++n3L/9gPfg\n" +
                "yW9Fv73vWfCQ9rD6kfWj+l6v3sN9UX2XHk943PNE+mSov+J3vd83PHV++tMfYX+0\n" +
                "D4wf6Hkme/bx+eIXZi+2v/R+2TqYOHjvVcGrodeVb8ze7Hjr/7btXca7R0PT31Pe\n" +
                "r/1z5J8tH2I/3PlY8PHjvwHpCOBOCmVuZHN0cmVhbQplbmRvYmoKMTIgMCBvYmoK\n" +
                "MzMxNwplbmRvYmoKMTAgMCBvYmoKWyAvSUNDQmFzZWQgMTEgMCBSIF0KZW5kb2Jq\n" +
                "CjEzIDAgb2JqCjw8IC9MZW5ndGggMTQgMCBSIC9OIDMgL0FsdGVybmF0ZSAvRGV2\n" +
                "aWNlUkdCIC9GaWx0ZXIgL0ZsYXRlRGVjb2RlID4+CnN0cmVhbQp4AZ2Wd1RT2RaH\n" +
                "z703vdASIiAl9Bp6CSDSO0gVBFGJSYBQAoaEJnZEBUYUESlWZFTAAUeHImNFFAuD\n" +
                "gmLXCfIQUMbBUURF5d2MawnvrTXz3pr9x1nf2ee319ln733XugBQ/IIEwnRYAYA0\n" +
                "oVgU7uvBXBITy8T3AhgQAQ5YAcDhZmYER/hEAtT8vT2ZmahIxrP27i6AZLvbLL9Q\n" +
                "JnPW/3+RIjdDJAYACkXVNjx+JhflApRTs8UZMv8EyvSVKTKGMTIWoQmirCLjxK9s\n" +
                "9qfmK7vJmJcm5KEaWc4ZvDSejLtQ3pol4aOMBKFcmCXgZ6N8B2W9VEmaAOX3KNPT\n" +
                "+JxMADAUmV/M5yahbIkyRRQZ7onyAgAIlMQ5vHIOi/k5aJ4AeKZn5IoEiUliphHX\n" +
                "mGnl6Mhm+vGzU/liMSuUw03hiHhMz/S0DI4wF4Cvb5ZFASVZbZloke2tHO3tWdbm\n" +
                "aPm/2d8eflP9Pch6+1XxJuzPnkGMnlnfbOysL70WAPYkWpsds76VVQC0bQZA5eGs\n" +
                "T+8gAPIFALTenPMehmxeksTiDCcLi+zsbHMBn2suK+g3+5+Cb8q/hjn3mcvu+1Y7\n" +
                "phc/gSNJFTNlReWmp6ZLRMzMDA6Xz2T99xD/48A5ac3Jwyycn8AX8YXoVVHolAmE\n" +
                "iWi7hTyBWJAuZAqEf9Xhfxg2JwcZfp1rFGh1XwB9hTlQuEkHyG89AEMjAyRuP3oC\n" +
                "fetbEDEKyL68aK2Rr3OPMnr+5/ofC1yKbuFMQSJT5vYMj2RyJaIsGaPfhGzBAhKQ\n" +
                "B3SgCjSBLjACLGANHIAzcAPeIACEgEgQA5YDLkgCaUAEskE+2AAKQTHYAXaDanAA\n" +
                "1IF60AROgjZwBlwEV8ANcAsMgEdACobBSzAB3oFpCILwEBWiQaqQFqQPmULWEBta\n" +
                "CHlDQVA4FAPFQ4mQEJJA+dAmqBgqg6qhQ1A99CN0GroIXYP6oAfQIDQG/QF9hBGY\n" +
                "AtNhDdgAtoDZsDscCEfCy+BEeBWcBxfA2+FKuBY+DrfCF+Eb8AAshV/CkwhAyAgD\n" +
                "0UZYCBvxREKQWCQBESFrkSKkAqlFmpAOpBu5jUiRceQDBoehYZgYFsYZ44dZjOFi\n" +
                "VmHWYkow1ZhjmFZMF+Y2ZhAzgfmCpWLVsaZYJ6w/dgk2EZuNLcRWYI9gW7CXsQPY\n" +
                "Yew7HA7HwBniHHB+uBhcMm41rgS3D9eMu4Drww3hJvF4vCreFO+CD8Fz8GJ8Ib4K\n" +
                "fxx/Ht+PH8a/J5AJWgRrgg8hliAkbCRUEBoI5wj9hBHCNFGBqE90IoYQecRcYimx\n" +
                "jthBvEkcJk6TFEmGJBdSJCmZtIFUSWoiXSY9Jr0hk8k6ZEdyGFlAXk+uJJ8gXyUP\n" +
                "kj9QlCgmFE9KHEVC2U45SrlAeUB5Q6VSDahu1FiqmLqdWk+9RH1KfS9HkzOX85fj\n" +
                "ya2Tq5FrleuXeyVPlNeXd5dfLp8nXyF/Sv6m/LgCUcFAwVOBo7BWoUbhtMI9hUlF\n" +
                "mqKVYohimmKJYoPiNcVRJbySgZK3Ek+pQOmw0iWlIRpC06V50ri0TbQ62mXaMB1H\n" +
                "N6T705PpxfQf6L30CWUlZVvlKOUc5Rrls8pSBsIwYPgzUhmljJOMu4yP8zTmuc/j\n" +
                "z9s2r2le/7wplfkqbip8lSKVZpUBlY+qTFVv1RTVnaptqk/UMGomamFq2Wr71S6r\n" +
                "jc+nz3eez51fNP/k/IfqsLqJerj6avXD6j3qkxqaGr4aGRpVGpc0xjUZmm6ayZrl\n" +
                "muc0x7RoWgu1BFrlWue1XjCVme7MVGYls4s5oa2u7act0T6k3as9rWOos1hno06z\n" +
                "zhNdki5bN0G3XLdTd0JPSy9YL1+vUe+hPlGfrZ+kv0e/W3/KwNAg2mCLQZvBqKGK\n" +
                "ob9hnmGj4WMjqpGr0SqjWqM7xjhjtnGK8T7jWyawiZ1JkkmNyU1T2NTeVGC6z7TP\n" +
                "DGvmaCY0qzW7x6Kw3FlZrEbWoDnDPMh8o3mb+SsLPYtYi50W3RZfLO0sUy3rLB9Z\n" +
                "KVkFWG206rD6w9rEmmtdY33HhmrjY7POpt3mta2pLd92v+19O5pdsN0Wu067z/YO\n" +
                "9iL7JvsxBz2HeIe9DvfYdHYou4R91RHr6OG4zvGM4wcneyex00mn351ZzinODc6j\n" +
                "CwwX8BfULRhy0XHhuBxykS5kLoxfeHCh1FXbleNa6/rMTdeN53bEbcTd2D3Z/bj7\n" +
                "Kw9LD5FHi8eUp5PnGs8LXoiXr1eRV6+3kvdi72rvpz46Pok+jT4Tvna+q30v+GH9\n" +
                "Av12+t3z1/Dn+tf7TwQ4BKwJ6AqkBEYEVgc+CzIJEgV1BMPBAcG7gh8v0l8kXNQW\n" +
                "AkL8Q3aFPAk1DF0V+nMYLiw0rCbsebhVeH54dwQtYkVEQ8S7SI/I0shHi40WSxZ3\n" +
                "RslHxUXVR01Fe0WXRUuXWCxZs+RGjFqMIKY9Fh8bFXskdnKp99LdS4fj7OIK4+4u\n" +
                "M1yWs+zacrXlqcvPrpBfwVlxKh4bHx3fEP+JE8Kp5Uyu9F+5d+UE15O7h/uS58Yr\n" +
                "543xXfhl/JEEl4SyhNFEl8RdiWNJrkkVSeMCT0G14HWyX/KB5KmUkJSjKTOp0anN\n" +
                "aYS0+LTTQiVhirArXTM9J70vwzSjMEO6ymnV7lUTokDRkUwoc1lmu5iO/kz1SIwk\n" +
                "myWDWQuzarLeZ0dln8pRzBHm9OSa5G7LHcnzyft+NWY1d3Vnvnb+hvzBNe5rDq2F\n" +
                "1q5c27lOd13BuuH1vuuPbSBtSNnwy0bLjWUb326K3tRRoFGwvmBos+/mxkK5QlHh\n" +
                "vS3OWw5sxWwVbO3dZrOtatuXIl7R9WLL4oriTyXckuvfWX1X+d3M9oTtvaX2pft3\n" +
                "4HYId9zd6brzWJliWV7Z0K7gXa3lzPKi8re7V+y+VmFbcWAPaY9kj7QyqLK9Sq9q\n" +
                "R9Wn6qTqgRqPmua96nu37Z3ax9vXv99tf9MBjQPFBz4eFBy8f8j3UGutQW3FYdzh\n" +
                "rMPP66Lqur9nf19/RO1I8ZHPR4VHpcfCj3XVO9TXN6g3lDbCjZLGseNxx2/94PVD\n" +
                "exOr6VAzo7n4BDghOfHix/gf754MPNl5in2q6Sf9n/a20FqKWqHW3NaJtqQ2aXtM\n" +
                "e9/pgNOdHc4dLT+b/3z0jPaZmrPKZ0vPkc4VnJs5n3d+8kLGhfGLiReHOld0Prq0\n" +
                "5NKdrrCu3suBl69e8blyqdu9+/xVl6tnrjldO32dfb3thv2N1h67npZf7H5p6bXv\n" +
                "bb3pcLP9luOtjr4Ffef6Xfsv3va6feWO/50bA4sG+u4uvnv/Xtw96X3e/dEHqQ9e\n" +
                "P8x6OP1o/WPs46InCk8qnqo/rf3V+Ndmqb307KDXYM+ziGePhrhDL/+V+a9PwwXP\n" +
                "qc8rRrRG6ketR8+M+YzderH0xfDLjJfT44W/Kf6295XRq59+d/u9Z2LJxPBr0euZ\n" +
                "P0reqL45+tb2bedk6OTTd2nvpqeK3qu+P/aB/aH7Y/THkensT/hPlZ+NP3d8Cfzy\n" +
                "eCZtZubf94Tz+wplbmRzdHJlYW0KZW5kb2JqCjE0IDAgb2JqCjI2MTIKZW5kb2Jq\n" +
                "CjcgMCBvYmoKWyAvSUNDQmFzZWQgMTMgMCBSIF0KZW5kb2JqCjMgMCBvYmoKPDwg\n" +
                "L1R5cGUgL1BhZ2VzIC9NZWRpYUJveCBbMCAwIDU5NSA4NDJdIC9Db3VudCAxIC9L\n" +
                "aWRzIFsgMiAwIFIgXSA+PgplbmRvYmoKMTUgMCBvYmoKPDwgL1R5cGUgL0NhdGFs\n" +
                "b2cgL1BhZ2VzIDMgMCBSID4+CmVuZG9iago4IDAgb2JqCjw8IC9UeXBlIC9Gb250\n" +
                "IC9TdWJ0eXBlIC9UcnVlVHlwZSAvQmFzZUZvbnQgL0NNSEVMRCtIZWx2ZXRpY2Eg\n" +
                "L0ZvbnREZXNjcmlwdG9yCjE2IDAgUiAvRW5jb2RpbmcgL01hY1JvbWFuRW5jb2Rp\n" +
                "bmcgL0ZpcnN0Q2hhciAzMiAvTGFzdENoYXIgMjIyIC9XaWR0aHMgWyAyNzgKMCAw\n" +
                "IDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAw\n" +
                "IDAgMCAwIDAgMCAwIDY2NyAwIDAgNzIyCjAgNjExIDAgMCAwIDAgMCAwIDAgMCAw\n" +
                "IDY2NyAwIDAgMCA2MTEgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgNTU2IDU1NiA1\n" +
                "MDAKNTU2IDU1NiAyNzggNTU2IDU1NiAyMjIgMjIyIDUwMCAyMjIgODMzIDU1NiA1\n" +
                "NTYgNTU2IDU1NiAzMzMgNTAwIDI3OCA1NTYgNTAwCjcyMiA1MDAgNTAwIDUwMCAw\n" +
                "IDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAw\n" +
                "IDAgMCAwIDAKMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAw\n" +
                "IDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMAowIDAgMCAwIDAgMCAw\n" +
                "IDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAw\n" +
                "IDAgMCA1MDAgXSA+PgplbmRvYmoKMTYgMCBvYmoKPDwgL1R5cGUgL0ZvbnREZXNj\n" +
                "cmlwdG9yIC9Gb250TmFtZSAvQ01IRUxEK0hlbHZldGljYSAvRmxhZ3MgMzIgL0Zv\n" +
                "bnRCQm94IFstOTUxIC00ODEgMTQ0NSAxMTIyXQovSXRhbGljQW5nbGUgMCAvQXNj\n" +
                "ZW50IDc3MCAvRGVzY2VudCAtMjMwIC9DYXBIZWlnaHQgNzE3IC9TdGVtViA5OCAv\n" +
                "WEhlaWdodAo1MjMgL1N0ZW1IIDg1IC9BdmdXaWR0aCAtNDQxIC9NYXhXaWR0aCAx\n" +
                "NTAwIC9Gb250RmlsZTIgMTcgMCBSID4+CmVuZG9iagoxNyAwIG9iago8PCAvTGVu\n" +
                "Z3RoIDE4IDAgUiAvTGVuZ3RoMSAxMzMwMCAvRmlsdGVyIC9GbGF0ZURlY29kZSA+\n" +
                "PgpzdHJlYW0KeAHVewl0VFW26DnnjjWmqlLzkKpKpapSmQcSEhJIGZIQhkQgCAkS\n" +
                "TIBAQFDAEAwKLyo0EBFFZFD8Kg6MaoqQhgLEpnko0m0r2Da2SNvaom37zLJfP7RV\n" +
                "UlV/n1tJhPi6l+uvXr3Wr5t95nvPPnvvs4dzb9qWr2hBKtSJGDR5ZvPS+Uj65VYj\n" +
                "RF6cu6R5abyeWIoQ3j63vc0Vr3OpCDGL5y9dsCReFx9DSO5YsLhj4H6DHSHbM60t\n" +
                "zfPi/agf8sJWaIjX8QjIU1qXtN0dr+t6Id+w+M65A/36d6FetaT57oH50WWou+5o\n" +
                "XtISH5+bAnnK0jvvaovXc16BvHXp8paB8bge8HsbYWhNRo8iGbodCYggDVyNCAmf\n" +
                "yx2IhV7aD7+ctz74xW0JpV8jrSjVb6t5WMp/c+z40W9b+v2KLeJ30CAbHE9zPhAN\n" +
                "IKTE0N+n2DLUI90HSXIY1aWH0XiAMoACgPT0m8yoE+9BjwA8A8CghfhB1AGwEeBx\n" +
                "AHaotB9qx/CDPawYPI47kBVPCCpY5zS9xWmWK5zvhDHf+5TzffMnJ7AFuPcxtvSo\n" +
                "kOwmOX4GP43mISd+AXnxKlSNUvEThwOLnU3QtR8tBegEYKQU4/09SXnOV3EG8rIY\n" +
                "7vGhJBYfcf45N9P5aW6Y4B7naX+YheyXSVALJjhPOZ5y/sKxwPkqwMF414EAjDji\n" +
                "3O9Y7NyaFMZP9DgfdYQx3LMlnq1wwK1HnEsC253zcqX+SdvD5GCPsxj6pwcVzsIi\n" +
                "t7PAccWZ7Q+LGOqZjknOtNzfOFPgRhjmgod6g1qn3bHVOQq6khyV/lEAJ/ABvAul\n" +
                "4V093gnO41CE5R4eHyjaHsb3HK5OzfWG8apgYXXq9kC13xuY5PQGqvx+KE9/Q1gr\n" +
                "3CrcJOQJ6UKq4BPcgk3QizpRI6pFpSgXRVEI4xd7ypz8CXwQlQFZDh4WeZEL45eh\n" +
                "kT2BX5IaXzoqsiIRkagPxz4C4cVIH8YHezW0BIUjvFTiw/ilw/Gml4JOlpZYqUND\n" +
                "aBkSSBHBIkETUAg/FObROmN7mblMN0ZbXFXxj5ImqWcwTf/HPzN2hLZPrKsPHXA0\n" +
                "hPJoIeZoGBxuHiz8w7xtBXS1lKenT5zacbh96aL5lS2eyiZPZQtAU+jB9lZzqHOO\n" +
                "y3Vo0VLa4QoxvqY5c1tp3twSWuppqQgt8lS4DrVL9w3rnk+72z0Vh9D8ymn1h+YH\n" +
                "Wyp62oPtlZ7miobDc8qXN94w18ahuZaX/y9zldOHLadzzZHuGzZXI+2eQ+dqpHM1\n" +
                "0rnmBOdIc9HFVy6sK7+rDaTTVblwoiuUWhcaP2VmfcjV3FARxnugsWIF4k4hDXcS\n" +
                "pXKdyMpmIydCsfcBLtE8ekvsM+4s0kSXxP6bKQGmHqNAomWl6BR6CO1C3YhH+6Cc\n" +
                "imajnegcXgR7exbqRRdxEsoC3cuiMJqE3sSx2AU0Hz0P49vQabQNHUJKuGcJMkDv\n" +
                "ZuyNrYJ6EMpz0NrYsygFFaGfoZOoGJ66GfXF9scOQ+9UdAs6gA7C/b/GHnKITYy9\n" +
                "HLuCRDQFnrkWei7EJsW6kQ5loHI0GVrXolexl7kUa0VmVALYPYmeRrvRL9GX+H7c\n" +
                "G2uNtcfOxz4GUTUjO6qDazXuxR8z3ezPYk/GvohFgRKpKA1mbUJb0XPw/G64ToFq\n" +
                "rcS34za8FW8jQXI/6WXXcaZoBOgQQOPgqkZ3og1AgWPoDPob+g5/RcyMhmljXosV\n" +
                "xP4HKdBEWCVdSQtqh2s9XJthTScwj3PwWDwZr8aP4W34tySN3ELqyUpyN/mMqWVm\n" +
                "MR3Mb9m72B5uE7eTV0S/jp2InY39DpmQA92KlqM1sLrT6Dy6ir7HDDzLjr24BJfj\n" +
                "2XB14l3kGN6Nj5HJ+BQ+Tw7gP+JP8Ff4GuGIkhhIOmkjW8lBcpq8xSxktjGPM39k\n" +
                "vmbHcITbzX3Ke4UPonOiG6NvxUpiH8e+BRUrIjdwphzVottQM6x2KRqB/gNW8RJc\n" +
                "3cC1M+g1dE66PsF21Ie+BSogrMNWnIdr4KrFN+P5eCF+Ch+H61UJl28IMILIiJaY\n" +
                "iJ3UkTlkCekkvyOdjI1JYyYwM5luuN5gLjLXmGssxyayBnYcOx5tYpewT8C1h93H\n" +
                "9rBvc8XcGK6Wm851chu5Tcxc7gJ3kV/Db+Z7+K/4v4JanCTcKWwC7pwDmf0lyPIP\n" +
                "PxanAPZ56A40F1fgOWg7cGM3bkZdIF3z8Aag11KUGmtk1jDjSA5Iw6voHpDWJ9Bq\n" +
                "tJGZhXbHfs8cQO+BpCyGR3aivWw5cnA7gDv3oxyQooErGEgLpPp93hRPstsFKt9u\n" +
                "s1rMJqNBn6jTalRKhVwmCjzHMgSjjEpPVZMr5GsKsT5PdXUmrXuaoaH5uoYm2Mqu\n" +
                "UNWNY0Iuel8zdN0wMggj5w8bGYyPDA6NxBpXKSrNzHBVelyh31R4XGE8c0o9lB+q\n" +
                "8DS4Qn1SuUYqPyKVVVB2u+EGV6W5tcIVwk2uylBVe2tXZVNFZgY+FgRyyDMzqOII\n" +
                "IgV9cAiNbV4NChaNpSMqQ1ZPRWXI4oEy9DHeyuZ5oclT6isrbG53A7RB09R6mCMz\n" +
                "Y2EI8EQPKud55j0YDqI5TbTUPKs+xDQ3hEgTfZY2PWTyVIRMqz41/1AdLFVuuq4z\n" +
                "RLxVzS1dVaFg04NAXFptorXmTVCbWOeCx5J1DfUhvG4ACYrjIsCUohu3Cd6mRa6Q\n" +
                "zFPuae1a1ATERVPre6xBq6R8Q2hyfY8laJEqmRnHzGtK3LD6Y5k3Zd5E8xK3eU08\n" +
                "//MD8fZ3TtHcvObMR5BPnDpEAEwp4BkPeIZcc6VJPIBsEU1ailDX3CKgE/waMCxz\n" +
                "IeAzNkRAZhhviPOObw511g2i0VoRR65pUUWPzGKVjFB5A4xv6tKMAk7BeI3H1fU1\n" +
                "WOsmT9+XN7Y0D7TwXs3XiHZSRg/JSgg3D5bbqbH0wqpbzZ5Wyt92iadQ95grr2uA\n" +
                "OiUNxTmkBwM+ud4dcjVAA3iTGRPDSDa5/hDGmxvCOLYujCocx8BHZW6bDd0ZVNQW\n" +
                "VsD8UMnMgIY0N5SyMlxVMHMVlRVXl6tr/LwuV5WrFYSJ9Uo5dLR0NWQDBevqgU5o\n" +
                "GswYbLANFVsaGkbBc7Lpc+AWGN7VAE9YNPAEyKWm7AgMyskAY8r4JtdPqQ91VthC\n" +
                "wYoG4AKI76nJ9aFTILkNDTAqdwhTwHj1QvMAznmAc24a9OfHnwK+Syc8oqGriz6z\n" +
                "rt7jDp3q6rJ10f0Wr4cxGt4QHGgIIzqEkjyMOyfDvZB53DaJB26PG9BqoDQdASI9\n" +
                "KFHgs/9zChcO4Q13jgRsCyUKF/2LKFz8Uyg86idRuGQI0xsoXAo4l1AKj/73UXjM\n" +
                "DRQu++cUDg7hDUjeBNgGJQqX/4soPPanULjiJ1G4cgjTGyhcBThXUgqP+/dRuPoG\n" +
                "Co//5xSeMIQ3IDkRsJ0gUXjSv4jCNT+FwrU/icI3D2F6A4UnA843UwpP+fdReOoN\n" +
                "FK775xSeNoQ3IHkLYDtNovD0fxGFZ/wUCtf/JAo3DGF6A4VnAs4NlMK3DlE4aAuh\n" +
                "6/Vw5zC1i/7linnWdSQHT4nToXJSDPl0lARx1wvsJ6gbyt2Qu9m70FSAdgi2SyAv\n" +
                "AqiGsXbIRwOsxWcpxC5BfyeUN/IH0FraDkDHtwOUkgNoI/TTOUxQ74SyAubR0RzA\n" +
                "ADACAM64IHyHAB5+SsTjDZC70C8GWqTm/8eESPcxkMI0P/pxAy38j3oGGwSIPmRI\n" +
                "DnGUEk6H1CgBzr20EPMlIj3EkkaIh8zIgqzIBmtwoCSI4VyDt/5/nrsH8C9Eheg0\n" +
                "TsHT8Yv4EnmeUTOPMP3s65yOO87L+SJ+Of8ngRU6hcviNPF12ShZp7xJHlLIFc8o\n" +
                "a5UbVEmqO1Tn4UkE4jfEnoe4n4Hzw7HxMz0xO4xYAFETRug8AK1DmbkMZcgFyBnI\n" +
                "ZZfRcbgLoenpx+FJHOQ5uflat9YPUM5uDvf/iTv5/dgwW3MNzohghiSQ6NEwF5E4\n" +
                "dyk4uRrX41bMbGB2sDvl++VhWVjOp8oxEngeE1Emg0SOBA5vwgzr0svlXh206TnO\n" +
                "q4MBCgXHyOQsz2EFwQwiSYIYxg1BGYRNvEzOcFDbF9SpVCaTlXsKPyW3KFW73Ztm\n" +
                "w6mQpfaquSYSsdRWtlR8VlVhRmWm0rLSmkhppFRbXIa1uuJi+NMWZ6/PSl+tmQje\n" +
                "HXvKFmLPNKzPMg80MNDAnGlIHxi7XlNaKgDk5uDGRtSIFTgxH3sYN+PBzOY/9q37\n" +
                "mBgubYucePpN8giZSTZGVjJzvx+Lw9FqiRovAF3mQkkFUrsg6Fyv3a4jeaIiKYGg\n" +
                "JJMo5iZarSqv2mKxXnS3bwT8a6/WRGo139T0obJIWSQ3Z2xH0IeNWq/BxwsccJ0R\n" +
                "iMDxco2Yh7EREplOkYcFPUS16ek4PT0tPf2+Rm/eyEJ6FWiIx61l3C6TUasXSACT\n" +
                "8y03tU0osSa8/9/Rp98gdTh777b6XdGfRboPGPx3NjxYNw5rcda1nVzie6ejF744\n" +
                "Ge0B3mKImRF7ANYgQ8VBs5DEsjImCQ4AZSJ/O7YqGNGLLHJFGM847N52eWAJpXQN\n" +
                "V1BZWWmfrjgb1pHoNri1FDzdzLX+N8mFSPZZ7mRvtLw7Mo/KKp3jW5hDDru8OViw\n" +
                "ULlQ16FcpWOr9fX6Vv0qPSuISVqNRo7VCXRuuUh4nZKV6fW5rNWYIAMUDMYwVgAK\n" +
                "g1SUUIhodaZiikakVAOIQIYbc3MaE915EJHzQB0P8vsgc+cVFnSTbWf+evHDaN5Z\n" +
                "pvPu8ruibXjTz/ZyJ//wxouxyFb22ChnlFn+CKUH7FP2HOAqoBFBK+aTkEBYUQay\n" +
                "iK4Rxsux13iLSIWxVnO15ipMeVUSRooFyCGIEaWC1l3AnotqfxXVcie7v/8bpwYC\n" +
                "0H00NXZZOvVIgPOsUvSHYFFaDpZrFDal3Z9frVkoW6QRikWdUsbY8oQUmUOjdJSk\n" +
                "k6xAydESUpKX5tVpBE60+5NN9jDuCnpMDqfgd2QpiKNAUSqUltr1QiBtX4p1jC1g\n" +
                "n5DgL7KMHvMK3gELOoa3ozjr+q72SbyLnAF6Ac599AImUkI2wpbI6svqo9tIayqW\n" +
                "pDO1cKQhGWGLFxcmuJE5yeZGRpfejd3JaCRxI6vD5IYFQwICmo41pfTM8r777oON\n" +
                "1JhizAdJHY3VOAHzAm/AVGZH+DzJAi94xuD8PDg20ephEEyhxp5kv89PM1/BiMKR\n" +
                "iVi9vPa2hu3u1rwlc3LrcO8Yg/KBVQ+VuOX7uL8/d7J9hcmrTNKmZfga04yykW/d\n" +
                "u+3k8R1db8/MGL9ni8HOq1X27AV4sZhhzpxVNymt7vVd1dU7IzvsyQyzTsmXe4LV\n" +
                "i36+YdvzifgKlc322IeslzsNVigJLQ1m7RH22t+zM8liQhIBc2ZycIJWnuRQKPR+\n" +
                "0eqyZmmycABpLU7XevfJRomowPcrVwZ2dR8lqRY0kEQ9s87Iy4283od1ckgMgsmH\n" +
                "E2VJvvhupmRKzNdSUui0eiJRwOBJiROJN+hNxvz27pLnm9747ptLq6blFe8h87ds\n" +
                "eeieY75xp7nTkf+qmRLti16NRkMlnpqNqz9/df+HRy7smH0I5IwgOOljzrO1kh3d\n" +
                "G8zea8E7zfvEA2ZmgqjdpWcYPe+wCiqHXmETbDaTxq/DjJ9orQ6532Sxw+sP4bB7\n" +
                "+eoBiYGVldb0FRfTrR6XGCho+qQFjkAW0as0yH1InaiBVWoTNIIFahxi3BgTllEY\n" +
                "VT6UoINEZuZ9mMW8G/SYJCpUWDSlkshI8oKMJk8WCACISlwq8qk4kAINyhfIxU9M\n" +
                "3Zrla16ckLPh0aUPWLqT/nrine+x7l07Wxt6b+4D+5Y8s/vyxpW/ew3nfwbHlKM4\n" +
                "oEFR7BLTB3xVIAdaGcwbqR6nnqHey+63cV5RTxIcGiQ6HEKinDhMCi4rMUsT0Oqs\n" +
                "ToXfaklyrncvL79++cBgBIy9nrdWs10mRxibFbA2OyTIQnxIbhN9sED4k3aBjoq3\n" +
                "JPS8AZmMJm2+1lNAl4UKRujyv3l09+rde1Zt2I+76nJGv/Rs2Yt3Ho5+/9WH+LbP\n" +
                "3zv36/88/ysyckTSROL4fsy2ufU48/sv8AzQIdXgIVrh5NQOp+xerAx27BAft+51\n" +
                "MpyaJHB6g1qXYNAHlUG9GLDiiYojzFn8OnPW9nvxfdlF5+89n5s+9yjOas/qyCyR\n" +
                "c6ckPGF0pBTzgmB0O+yC3GFUeIUd9r32o7AHWK8xwWvnLHKloFX7Exx+zupPyRL8\n" +
                "FovP/657T1z4QfYl0X83IlleyQBnNw7JCdXMfaBPJGmpQh6WY+BYGnMs7/RpNTpN\n" +
                "okavYXmlN9mW4gNPz+HDSQ6ZSfAhhUHtwyq1x+qGJg4S0QxypdJAQlVNXNdIwpOW\n" +
                "nnYfXtaIloH9NgKNjQZ3EmwpMJEgQKBreKC2FoQI+8AWJPMCJr0Xiwp1mv6vuEd2\n" +
                "PDQtR39IuDl3asdNU9+IfoHNf8JOReqEl+7dx2EPO+72W6YsnvDsc681Fo4r2ZI1\n" +
                "2a7BHjibJ7g86ltRdf/hLkxf/oLNAH+bmLh3wHutCaYLDl7uYHCCvtio4nVyC5gO\n" +
                "tUobMOkEXYLaqSbqfr3FbOl3L1gTF7FIY/EZars01xuSsr53YYvpRhbm5xlNBrov\n" +
                "eEO+wQOmxVOQX/BzT1mvNsVktyimunp6e7Zt48pHzCLkeYJveXlz/zzmyc37JHsz\n" +
                "OlrCfA6y4kSZ8DbnaLCmUD9eHC+rFxtkG5T7bfsc+/170o/ZFEGRMSYH1GfkyWBS\n" +
                "WD7gsMh1DnlClpCVxdmZLGNWZoCz5ijVftUYn99uyc65boNc7SumEhC58jXwedCm\n" +
                "lPVJbI/zPcOTak1SaFO8Gp8nyedDqVZItAq1GyWolSqvI9mH/bYA6AmlDozvD9oB\n" +
                "StIuojunIB+cHN6d7PPnA4speyVrkUI5iySjImkNMDGY3Ds7v2BP6dLouZe+VB9V\n" +
                "+Uc/8HbQxxTuXP1y9BoWjuOK5//j1Srv1ntP35wRvcCWj/GMXd+f92b7pV0vVPtL\n" +
                "H53+h6mT/44dWIWzortP9dz2xM9Pds9dSzIlPq8FI051ihHVBTNg14gmwST6WX/i\n" +
                "CmGFKCaqSKIBIa2DFwxKuSogt5qxIYCMFpMZ3oofds+J6xTqJwyYi1JJoxRjukEk\n" +
                "YwA2Mm4YPdoRkrgatJ61vcH8Gff/pS7zWFLu+qVHekH5X57iLn6u4anIFPJc+8j6\n" +
                "Jy5G3qBySOCNGcIl4LvQmKAwaBc+ZUE4eUZO3ReQ24DAgMKWHfgBkzOR0jNDYldW\n" +
                "AyIHFsmjBUlbexR+bNq1i9zJN+mzY5eik3GR9GwtjSvqIHSgoj8dvh/ANLY4H48x\n" +
                "ZOfhjaEaBmizjwMeNMbIycUgtHgMLgD9B1wEI+/HRb290Wc7cnt9ZSGVw8n2nf9u\n" +
                "BOuZxR65NnLFqDmE3ggP7wR6U79RgfYG5zUQPErEFgKb2sTP4BZwHfzdwnruGHOO\n" +
                "ucTIOY6HF+Qyhqwlj8FGYEgxhBwsBy9j+CU64JQowGsZjpeJHJglOfh4DC8XIOCy\n" +
                "qmREHkAKCDJ63HOOYWPcU6JMKrXUaj6DGKMUYowy6iFhgPU1Wenias0vWRpSNHKr\n" +
                "Nac0YqkoxQ+ggpYD+XC+DNSEoPV0voTf+iw6Hx/6LNqz4yXuZP9BfDZ6Z2QOsXdF\n" +
                "75DWtxEWSeMqBgWCIDmwCmAU8AkxFpa7jk0gMDTiAbUaZ9DG3l4aoEnPAJ7zXnYc\n" +
                "8qF1wRJBFNR8gkk0qU0JftEParvaMl2xQKH0eOVWh8ciJ6zJ63aYHCpeQLzN7mUS\n" +
                "5akwpzYAHwngHmuAfhsRBLuW5YUNafGnhrHqesG9ornadzUygAzEXuD09oF+B9LA\n" +
                "5h+UYsOAFJsGvTwQZqp6QZavk+qe4IiGZZ21GSmlz7b8vjbtxO01ix4/ag0snb+3\n" +
                "l83eeXPK6LKUqul1T07bHBlJPr998uY9kS3kxJK8iU+9TaVdknWmD3QbPS2YHcw9\n" +
                "yp/lCcvreb++nW8TOL2S6M0a8N4Qb1bIrYLVipQBmdWOs8wBC7LYwIW+YUvGzVhc\n" +
                "g8G6+rTFg9sSg5dmuG4pdF+CXVFjWA9ee3DSgdYrkzOOOnLWBAMTijJtvXgv4D97\n" +
                "6tMznqX7c07pPJWxvGDZwsjbgCxwuiT2PusG30wpnXQ8EszfKW7XPG58gd0n7tHs\n" +
                "N4bFN8T32E/Vf9ErR4m8wywoHTqFRbBYDMSfYLXJ/AaL1RbGMvDQBiyw5NH/4J1J\n" +
                "pjYDjod8ikQZWEst8WHBBCVOBSW5XumD/QqJaASHjFFDItlTmqSDI5aiKxjQPOCF\n" +
                "6cByEjd4K5IT9tG6nEnHX9i+/Tn4OKA/+vc/RPux7s98G07Ys332Y/09B68wl6Jf\n" +
                "gksaib6M0/vB8Q9SP6w9egvrhaWr4XuntmDGfnGviaSKLrtWzTsMQgKvdtgVyWri\n" +
                "N1tT5OBduwPJCRZPyv/qXUsumFaSM4ic7UYb4qw+1odssDDOCAm2qH2IMUlrkpZF\n" +
                "fWzqUcd5JvnUOD8un/DClqoiCDu0HvL6Xm/V8ROVXkijWd2FwVvvORI92vZEx9Sc\n" +
                "kt6O377TOevQiXlP3DtjD3No8/jU0uhfYI3Pbr+tIGl85A9UFkujt4AsjoM1utCq\n" +
                "YH6Rudpcb96H93L77HyqqDMxCodLSOQZh1VhVAvgbBoDBr01We13WNzJ19lSydeW\n" +
                "nM2BpcYNqN3mVKrgYxwfscH6lE5IkJ0BdyhJMeBtDribccs4yMABu+nRSh4nuFv5\n" +
                "3/ore14Z508fH16xFz98a17WwZ9nPr3yYPRvkXN4zey9oeYdDzY+/et3yZixKVXb\n" +
                "vvcRX/UtWAlv/jGeMKivyKOwTi26OejzMz7VSGYcy6pFDVHLtDKlX6TbTSsXrYmY\n" +
                "+tPIoksM40pQIHFXh0ZKoFJBjdWUnYmcAZUh6Y247ZO22JCvo/VsPGh4/nbO7NDY\n" +
                "NBseBZVwrHAXYV5lSPfyyE5K8/LYe8wRdiL4Ndk4K/hwkWwnt133uH6nYWcan5ri\n" +
                "9Re6q9zjUsb5p6fM8M9PWeDrUHaoOtTtnraUNm+bb0/SvoxEBtxMLpPNSkRWg81k\n" +
                "Nxsy9VmpCYqFos9b6CXeZJWcTU80v253JAqsI+uJdEW2IFNriICy3dlWp9lo9pvG\n" +
                "pPoEf6o1V+30a8Ygf5YlJ7dnyDcGVRn3jYo1UKLLLc6GFFQLdZBp5E1V5zJpx07C\n" +
                "mcRn8Fp9brXTjWTw+RdmMiB259Kg5NBBm01vdmNXQrIbuZPVKtEvd2OfVybHmawb\n" +
                "vvmDJElrd2OLERLJRZaCKymRtsLgBofQPFFyoaRtkU3dYgi5qdcheOIuMt0mTkw9\n" +
                "aThEobb6K9FbsW/eztH+ux7eeFPbB8f+dvtYcoDzjXl8/sLK1NqVp8sXvv/hV2cF\n" +
                "fBRPnpkzY8atlSkQVSSnjb9v5yubZ7aOzhtXG6xKsyQ6sjMqH3v4/PvPkO/Atpti\n" +
                "XxEZNxO04NSfq7Lkp9Q4jMuCXtZYbGJ4tVxrBbMEX8IEkEFtSGCcDGH6jXCSBn7z\n" +
                "QGQ6zG/Ojp/A9GkiVyQjSb1lGkMPni/4CqjrvO/IwYM+Q64qSe8c618zc8sWbmb0\n" +
                "d1sjlUWJCkw2y8T7FpDXtoKsE9QZ+4T5EPQWPZGeHRwV1r+hJ7JEUW9JtOhT+ZXM\n" +
                "e+BUIE4tR7xKzoGONgtmM4S7WfKAUmG14gBF9p1BT6uGKmkq/sD+uI9cVkoFgoo+\n" +
                "bsRxRMF5pQHiSClmAa5ovbjImvPAKxXe3gPEM2LB1k/rMnE3mx0pnjqiad/M/0PU\n" +
                "1y48NTpt2uNTN5LfW6m/pAAD8wWbDSf+JJhVjl/DBC1AraSVWcCvZzdwe9E+IsIX\n" +
                "TqSSncD9jN3InWXf4MTxqXel0hNYMClSSAKf1IVjS3shSHOxYfzAUYZZoiOYwHeG\n" +
                "DwSTePCmYCaOZxmMOcLwDAIXSy5SZnWT45h6oGsP427eEj+r/eijgdNa6kfBaa1u\n" +
                "wFMQwI3S1F6pEeJZ+sQpHUEvCegYhkUBOCSGGPGGh4PT1s2hH55bXBwpLo6fAw89\n" +
                "mRM06fAHZ3AQDjYuS5RhOMrFl3ESTn8tuvhUdAWb3b+Tab12ASiE4Z0D4nZDSYld\n" +
                "wTXj2AMyYD+uEsYr1jNd4jr5r8gZ5nXhnPi6/JxCMV9YJLbIFyrahQ6xXd6hWCd0\n" +
                "KeR0LBnHrER3c8yMVGMqRP1sCS5hH8YPs7yMxYyCgMOp5BAvyhWMIFcDjeB0d5fI\n" +
                "sGfkRHZGgfAupUVFaQ6OJj3YlhYVT4eWBk4WUA1O5yiFlBzQRoAv1XRKpYJbr0mH\n" +
                "P2BXrwy+O5KH8YPBRNDVROBZjg7kBZkokwNnHwyqdSwcxihh2dKtmHqx6zWrz5g5\n" +
                "6saCP/uaVFi/WnNmqIUeiS9btgy8WhvJt1FaKoCc77114VfvfNAbPXfi0m9PRH8N\n" +
                "JO1lJvUfY8Zdu8CM7v9PIOiAHH4MRQUqoJECRAQ0ZGAAeAAZRAYEvrdDsAGOw3uk\n" +
                "wZI4UMrJTYT5TOBFU0c66S/ffPdBdAfu+Cz6TTR6BXew2dH1uIOLXIt8gB+N3kG8\n" +
                "sF3heYboeCnWpe+PfhW8o8uwwbzXzNBYoUhXravXLRBWMiuFTfqdaAe307DDuMO0\n" +
                "D+0zaqrRRMM40zkDW8G9zpH13B60h9ptE5eSypkNJiPETwalIsEhqqkTZrQBE6kc\n" +
                "mgzmbuXDRvDF3o3vGhD3mivmG5gX3+rA1jxLthmiCBD+YgzsCuoM8KrLuERnMpk5\n" +
                "jOmGMsMLC8oOmomQA+Vzc5Zh+kYin2eIQCRFXEAPNgpHjsEjgRsM4z7re2BO+ZOd\n" +
                "T/oCSdlpmrxsDTdGHW17Ezsxm70guiX65cvR+b28+LyKd5vFx1LYWhD/+ymtRoCe\n" +
                "6AXdRt/ELQ6Wj+Sr0QxUj2fwoC3wAn4lJ4MdzgfoTpfLMQ+HNpgUQ8QE73eKQaTk\n" +
                "AjdGsCqZCciiUPYMOaKSCy29FoiUFtNDeWmvg82DVa1f/RqVp5HYXeA2YHhVgEeQ\n" +
                "eyK9zJjIRtLV34nf3syg3VsjsCPHg48M5ynM3dJ5ig3OLpqDhbZPLeiHcxUHHKw4\n" +
                "tXI38MKWFDA7f3S84nK/414wcII3ZCYuAkYDkQsEdDTAo4csZX34H5+zeAvyDQKo\n" +
                "5B+dt5DEXvj9+NTF+eabZ69dlOSRSiWKtaAWqTA8SYYGBk7w/PDVaT4aiSpQJaqS\n" +
                "vj4dDx9c029Ma+D7zJul72Cnwret0+CL0+mUSagBzYQvRmfBfwuckh5KNRmWSjy8\n" +
                "S0Vja6orJ1WkV7csbm9pWzi3GXrivXQIvOCA70wRehHgFQAa2V8G+BKgHwYqAewA\n" +
                "GQClAJMAZgEsBrgXYBPAkwAvArwC8CbAZYAvAfpBsJQAdoAMgFKASQCzABYD3Auw\n" +
                "CeDJ2MAP5kRDZYxcw+o3DatXDKtXDatPHlafNqxOKXH9fHOG1ecOq88bVpf4eB2+\n" +
                "84f1LxhWbx1WXzisvmhY/fZhdaDZDfhK/89y3fz0BOH69dw5rL50WH3ZsPryYfW7\n" +
                "htWl/1+5br4Vw/rbh9VXDqvfPazeMay+6sb6NUlK/y8OukfGCmVuZHN0cmVhbQpl\n" +
                "bmRvYmoKMTggMCBvYmoKODYxOQplbmRvYmoKOSAwIG9iago8PCAvVHlwZSAvRm9u\n" +
                "dCAvU3VidHlwZSAvVHJ1ZVR5cGUgL0Jhc2VGb250IC9NWkJGWksrSGVsdmV0aWNh\n" +
                "IC9Gb250RGVzY3JpcHRvcgoxOSAwIFIgL1RvVW5pY29kZSAyMCAwIFIgL0ZpcnN0\n" +
                "Q2hhciAzMyAvTGFzdENoYXIgMzMgL1dpZHRocyBbIDI3OCBdID4+CmVuZG9iagoy\n" +
                "MCAwIG9iago8PCAvTGVuZ3RoIDIxIDAgUiAvRmlsdGVyIC9GbGF0ZURlY29kZSA+\n" +
                "PgpzdHJlYW0KeAFdkMFqwzAQRO/6ij2mhyCnZ2MoKQEfkpY6/QBFGhtBvBJr+eC/\n" +
                "r6SUFHrQQbN6M7PSx/69Z59If0qwAxKNnp1gCatY0A2TZ3V4Jedt+r1Vzc4mKp3h\n" +
                "YVsS5p7HQG2riPRXRpYkG+3eXLjhpWgf4iCeJ9p9H4eqDGuMd8zgRI3qOnIYs93Z\n" +
                "xIuZQbqi+97luU/bPlN/L65bBOVGmTg8KtngsERjIYYnqLZpuvZ06hTY/RsVpTR/\n" +
                "JtlVJIfU9Wp+8fWM5w/EEItPPT//umPHCmVuZHN0cmVhbQplbmRvYmoKMjEgMCBv\n" +
                "YmoKMjA3CmVuZG9iagoxOSAwIG9iago8PCAvVHlwZSAvRm9udERlc2NyaXB0b3Ig\n" +
                "L0ZvbnROYW1lIC9NWkJGWksrSGVsdmV0aWNhIC9GbGFncyA0IC9Gb250QkJveCBb\n" +
                "LTk1MSAtNDgxIDE0NDUgMTEyMl0KL0l0YWxpY0FuZ2xlIDAgL0FzY2VudCA3NzAg\n" +
                "L0Rlc2NlbnQgLTIzMCAvQ2FwSGVpZ2h0IDcxNyAvU3RlbVYgOTggL1hIZWlnaHQK\n" +
                "NTIzIC9TdGVtSCA4NSAvQXZnV2lkdGggLTQ0MSAvTWF4V2lkdGggMTUwMCAvRm9u\n" +
                "dEZpbGUyIDIyIDAgUiA+PgplbmRvYmoKMjIgMCBvYmoKPDwgL0xlbmd0aCAyMyAw\n" +
                "IFIgL0xlbmd0aDEgNTA1NiAvRmlsdGVyIC9GbGF0ZURlY29kZSA+PgpzdHJlYW0K\n" +
                "eAG9WHtwFEUe/vU8djcknEkA2SQsM3vDkrcQVC4QDpbNbkhIwECA20WQ3SQbk5hI\n" +
                "CkNOsOC2FDxZkFMRTsFSuYcn5JBhQ+EEToyUllp3Kmrp+apSztfVlZT34kpFM/f1\n" +
                "bLISSqn8QTldPb9nd3/9dW/PzHavWx+lsRQjkepXRrpayLrGPQBR0NQZ6Ura2X+C\n" +
                "zG3q6VaTtlxAJHa0dN3YmbQd9xONcd3YsWGoffYH8He0RiPNyTh9DTmzFY6kza6B\n" +
                "nNLa2X1r0s4+CunoWNs0FM9+C7atM3Lr0Pj0Hmz15khnNJk/rhtyStfaW7jENa4O\n" +
                "t4KuddGhfBYEvleIwSvQfZRGN5EdWibKaiL738e4SEKUx3FNf/ndp9dcMeccZTks\n" +
                "e82iX1nypf7jT34R/To//V7Hl3CkDedzaSscLCTKYIifTb83FbHa4SYY1FBsUA3q\n" +
                "PNRrUYuL5zspxh6je1AfRRWpjW2nDajbUB9ElVLaAVj9bHtCcniPsw2UyxZ60yVl\n" +
                "2fgcxTkmXXnNYLajDytvOz88wXKwemdYTmIspc0fwx5lj1AzKez35GEbqZoK2N6+\n" +
                "wg4ljNAB6kKNoYrWnbEDickzlJOshDwSQ5upNFlix5RPy0qVj8sMgSWUU/mGBPHM\n" +
                "ZFjeK5QB18PK064blZOovcnQwUJkHFMOuDqUXZMNtjeh3OcyGNrcmxTrXWh6TOks\n" +
                "3KM0l1nxuj2G0JtQZiG+wpuuzCx3K9e6PlKm5RsOBrvUVacUlb2kTEFDpKno1OPN\n" +
                "Uia5dimzEZrsCuTPRj3BDrJ9VMT2JTwLleNQMd2+msLyPQa7ra+6oMxjsI3emdUF\n" +
                "ewqr8z2FdYqnsCo/H/qKF+xb7Nfb59tn2IvtBfapdrc9zz7eke3IdPzIkeEY43A4\n" +
                "7Ab7Y2KeYjvBemkeaOntc9gcssGegFM6wQ5ZzkNPOiSH4CDHeMP8AJuX0XiD9R7N\n" +
                "5BqUYzZLsxnsUF/SdcirSFyTrECmwHXccCeBOQRaSDq727DR1it75jnnZc/NmlXl\n" +
                "/75b2IoM34u//3Iyl76ntiGoH3SF9BlcMV2h4XTnsPK9sns9QlFfcXHt0g19PV3t\n" +
                "LYGoFghrgShqWN/e0+rUY42qeqS9iwdUXZwabmxq5TIS1bu0qF9v1/zqkR6r3UXh\n" +
                "Fh7u0fxHqCWwLHikxRv1J3q8PQEt4g/1NfrWrR4x1rbUWOt83zGWj3e2jo/VaLW7\n" +
                "aKzVPNzIx1rNx1rNx2r0Nlpj8ckH2hp8t3Rjd6qBtlpVL2jQa5asDOpqJOQ32GNw\n" +
                "+teTPECZ8lNUIMcoV5pGCpH5Nuo7XA4uNz+Rn6fMwU7zX2IFFrWfV2Fw3hwaoLtp\n" +
                "Hx0mGz0OvYBuoAfoRdaO3/YqOkpvssl0Fc5eiQyqo78w03yVWuh3yO+mU7SbjlAG\n" +
                "2nTSBER3Mo+5EbYXeiNtMX9DU6ic7qSnaBZ63UlnzQNmH6JLaTkdpF60/zPThCPS\n" +
                "OPMJ8yNy0BL0uQWRV8068zBlUwn5qB7eLXSSecR3zFZyUgXQPUSP0H56hj5jt7Oj\n" +
                "ZqvZY542z2CrOmkSNaBsYkfZGfGwdKf5kPkPcxBMFFARRg3TLvot+j+MMoCjNcBu\n" +
                "Yt1sF9steIXbhaPSVnni4DfgoZAWoFTTWroLDPTTs/Rv+pJ9LjjFTLFbfM681vwP\n" +
                "pVMtZslnEqUelF+i7MScTjAbm84qWT3bxO5nu9nrQpGwXAgKPxduFT4RF4urxA3i\n" +
                "69ItUkLeIT9gSx88Z54wnzffoInkoutpHW3G7E7RafovfcVE9DWJeVgF87EbUGJs\n" +
                "n9DP9rN+oZ4NsNPCQfY++5B9zs4LspAhTBCKhW5hl9ArnBJeFtvE3eKD4vviOWmu\n" +
                "LMj75Y9tHvu7g42D2wZfNivMM+YXOGId5MbK+GgxraEIZttF19AvMItDKIexas/S\n" +
                "c/SiVT5kk+gsfQEWiGWzXDaDLUJZzK5jLayNPcyOo5y0sPxPwEIIaUKWMFGYJDQI\n" +
                "jUKnEBPeEGJinlgkLhRXiodRXhDfFM+L5yVZGidNkBZINbRD6pT2ojwmPS4lpFfk\n" +
                "WfJcebG8Qo7J2+QdYpP8qvymbbNtpy1h+9z2TxyLdfa19h1YnRexZ5/BXv72ktgU\n" +
                "oJ9BN1MT87NG2oPV2M8iFMfuamZ3ga8uKjBXi5vFBcJ07IaTdBt2617aRNvEVbTf\n" +
                "fEs8SH/FTulAlzH6g+Qjl/xrrM7tNB27aKh4C4sKC/KneqZoP3arOPIn5eXmOCde\n" +
                "OWH8uOyszLEZ6WPSHHabLIkCo5KAVhVW9alhXZqqVVeXcluLwBG5wBHGT1nVq0bm\n" +
                "6CpvF0FoRKYXmS0XZXqTmd5UJstU59Cc0hI1oKn6S35NNdjKJUHod/u1kKqftfRF\n" +
                "ln6PpY+F7najgRpwtvpVnYXVgF7V0xoPhP2lJazfCzrGlJbwg8NL6bxjnSojm3DA\n" +
                "UiXPCOi5mj+g52jQERM9gUizXr8kGPDnud0h+OBaGsQYpSVtOnDS9oxmrXm74aXG\n" +
                "MNciq4K6GAnpQpj3lVWsT9T8+sSNHzu/NYe1wI4LgrrgqYpE41W6N7wd5HIzzK3I\n" +
                "Dli1DSq6FbaGgjrbOgSCY2wHUg43+UzwhNtVPU3zaa3x9jDIpaXBRK431zp8daoP\n" +
                "JnK8OZZRWtLv3Fzhxuz7S+eXzueywu3cnJSf3pH0vzbApXPzsx9A1i5NEcA4A1oN\n" +
                "cOpqkzWIBrDl/BYtp3hTOXjCFWKYZhvwVOoC9ozo0WVPTUSPNQzDaPUnwYXb/Ym0\n" +
                "nFzrIeQLIT8cz5yNlUJ+pqbGz+FpHdbOfjbSExny2DyZ54gH+UKn9orOIsN6D39Y\n" +
                "ejDrVqfWyte3x1pT2JozcIEDNqeGY9bH4wFeH3TraggOvE2W1BqUVh88wtjOkMHM\n" +
                "rQb5Xf14RxXX3IBwCd9qbX6MD6O0BI4iN7SrStQqjFzF94oaV+M1zXG1Sm3FZpI8\n" +
                "lkQgGg9NA4MNQfBEyzCiN5SXUqOh0Gz0M433gyZIj4fQQ/tQD5CWa9o3SJpegoep\n" +
                "OLU+uCSox/x5utcfwipg+w7UB/UB7NxQCFllKaRAvKnNOYR5BjCXFSF+dbIXvLvE\n" +
                "0EUoHud9NgQ1tz4Qj+fF+e8taRuMLnZ4hxwG8RROucFi9WgLobnzrDVwa27ACnFO\n" +
                "r8GWHt5ReGe/NMMzU7jR8idAO9NiuPwyMTxrNAzPHhXDFSmkIxieA8wVnOGf/nAM\n" +
                "zx3B8LxLM+xN4QbI+UDrtRj2XSaGK0fDsH9UDAdSSEcwXAXMAc7wgh+O4eoRDNdc\n" +
                "muGFKdwAWQu0Cy2G6y4Tw4tGw/DiUTF8XQrpCIbrgfk6zvCSH47hpSMYbrg0w8tS\n" +
                "uAFyOdAusxhecZkY/tloGA6OiuFQCukIhlcCc4gzfH2KYW+eTheew7GLjl267Afz\n" +
                "qgsox5uSnE0+YRYU/vmMD2hcGfiyyIB0pzz4vwmF///jI5JO49tNxH9Alcn/ZRzT\n" +
                "DJJQHZkG0WlUbkMX34MOaYcUIdPeo+NoRbSi+Dh6kiGnl12d5c7KR/VJO42v/yY/\n" +
                "9VWlIS06j+98ZFiXGcV3y3ddPJ7MYfgCSyK34T8pWrTSV7Wytrg62tET7W5riiAv\n" +
                "GeW9IE6TzKGLOy7U/w/jLWQVCmVuZHN0cmVhbQplbmRvYmoKMjMgMCBvYmoKMjcw\n" +
                "NgplbmRvYmoKMjQgMCBvYmoKKFRlc3QgVGlsZSkKZW5kb2JqCjI1IDAgb2JqCihN\n" +
                "YWMgT1MgWCAxMC45LjQgUXVhcnR6IFBERkNvbnRleHQpCmVuZG9iagoyNiAwIG9i\n" +
                "agooTWlrZSBIb2xkc3dvcnRoKQplbmRvYmoKMjcgMCBvYmoKKFVuaXQgVGVzdCBT\n" +
                "dWJqZWN0KQplbmRvYmoKMjggMCBvYmoKKFBhZ2VzKQplbmRvYmoKMjkgMCBvYmoK\n" +
                "KEQ6MjAxNDA5MTUwMzI5MzJaMDAnMDAnKQplbmRvYmoKMzAgMCBvYmoKKFJvbWVv\n" +
                "IEFscGhhIFRhbmdvKQplbmRvYmoKMzEgMCBvYmoKWyAoUm9tZW8gQWxwaGEgVGFu\n" +
                "Z28pIF0KZW5kb2JqCjEgMCBvYmoKPDwgL1RpdGxlIDI0IDAgUiAvQXV0aG9yIDI2\n" +
                "IDAgUiAvU3ViamVjdCAyNyAwIFIgL1Byb2R1Y2VyIDI1IDAgUiAvQ3JlYXRvcgoy\n" +
                "OCAwIFIgL0NyZWF0aW9uRGF0ZSAyOSAwIFIgL01vZERhdGUgMjkgMCBSIC9LZXl3\n" +
                "b3JkcyAzMCAwIFIgL0FBUEw6S2V5d29yZHMKMzEgMCBSID4+CmVuZG9iagp4cmVm\n" +
                "CjAgMzIKMDAwMDAwMDAwMCA2NTUzNSBmIAowMDAwMDIwNTU3IDAwMDAwIG4gCjAw\n" +
                "MDAwMDA1MjIgMDAwMDAgbiAKMDAwMDAwNjk5NyAwMDAwMCBuIAowMDAwMDAwMDIy\n" +
                "IDAwMDAwIG4gCjAwMDAwMDA1MDMgMDAwMDAgbiAKMDAwMDAwMDYyNiAwMDAwMCBu\n" +
                "IAowMDAwMDA2OTYxIDAwMDAwIG4gCjAwMDAwMDcxMzAgMDAwMDAgbiAKMDAwMDAx\n" +
                "NjczMSAwMDAwMCBuIAowMDAwMDA0MTg4IDAwMDAwIG4gCjAwMDAwMDA3NDYgMDAw\n" +
                "MDAgbiAKMDAwMDAwNDE2NyAwMDAwMCBuIAowMDAwMDA0MjI1IDAwMDAwIG4gCjAw\n" +
                "MDAwMDY5NDAgMDAwMDAgbiAKMDAwMDAwNzA4MCAwMDAwMCBuIAowMDAwMDA3NzQ5\n" +
                "IDAwMDAwIG4gCjAwMDAwMDgwMDAgMDAwMDAgbiAKMDAwMDAxNjcxMCAwMDAwMCBu\n" +
                "IAowMDAwMDE3MTk4IDAwMDAwIG4gCjAwMDAwMTY4OTUgMDAwMDAgbiAKMDAwMDAx\n" +
                "NzE3OCAwMDAwMCBuIAowMDAwMDE3NDQ4IDAwMDAwIG4gCjAwMDAwMjAyNDQgMDAw\n" +
                "MDAgbiAKMDAwMDAyMDI2NSAwMDAwMCBuIAowMDAwMDIwMjkzIDAwMDAwIG4gCjAw\n" +
                "MDAwMjAzNDUgMDAwMDAgbiAKMDAwMDAyMDM3OSAwMDAwMCBuIAowMDAwMDIwNDE1\n" +
                "IDAwMDAwIG4gCjAwMDAwMjA0MzkgMDAwMDAgbiAKMDAwMDAyMDQ4MSAwMDAwMCBu\n" +
                "IAowMDAwMDIwNTE3IDAwMDAwIG4gCnRyYWlsZXIKPDwgL1NpemUgMzIgL1Jvb3Qg\n" +
                "MTUgMCBSIC9JbmZvIDEgMCBSIC9JRCBbIDxmMGU3NjU1NjYyNmJiYTNhODZiY2I3\n" +
                "NjBiNjMyZjFiYj4KPGYwZTc2NTU2NjI2YmJhM2E4NmJjYjc2MGI2MzJmMWJiPiBd\n" +
                "ID4+CnN0YXJ0eHJlZgoyMDczMgolJUVPRgo=\n";
    }

    public static HttpHeaders getHttpHeaders(final String apiKey, final String username, final String password) {

        return new HttpHeaders() {
            {
                if (username != null && password != null) {
                    String auth = username + ":" + password;
                    byte[] encodedAuth = Base64.encodeBase64(
                            auth.getBytes(Charset.forName("US-ASCII")));
                    String authHeader = "Basic " + new String(encodedAuth);
                    set("Authorization", authHeader);
                } else if (apiKey != null)
                    set("api-key", apiKey);
                setContentType(MediaType.APPLICATION_JSON);
                set("charset", "UTF-8");
            }
        };

    }

    public static Map<String, Object> getSimpleMap(String key, Object value) {
        Map<String, Object> result = new HashMap<>();
        result.put(key, value);
        return result;
    }

    public static Map<String, Object> getRandomMap() {
        return getSimpleMap("Key", "Test" + System.currentTimeMillis());
    }

    public static Map<String, Object> getBigJsonText(int i) {
        Map<String, Object> map = getSimpleMap("Key", "Random");
        int count = 0;
        do {
            map.put("Key" + count, "Now is the time for all good men to come to the aid of the party");
            count++;
        } while (count < i);
        return map;
    }

    public static void waitAWhile(String message) throws Exception {
        if (message == null)
            message = "Slept for {} seconds";
        waitAWhile(message, TestFdIntegration.getSleepSeconds());
    }

    /**
     * Processing delay for threads and integration to complete. If you start getting sporadic
     * Heuristic exceptions, chances are you need to call this routine to give other threads
     * time to commit their work.
     * Likewise, waiting for results from fd-search can take a while. We can't know how long this
     * is so you can experiment on your own environment by passing in -DsleepSeconds=1
     *
     * @param milliseconds to pause for
     * @throws Exception
     */
    public static void waitAWhile(String message, long milliseconds) throws Exception {
        logger.debug(message, milliseconds / 1000d);
        Thread.yield();
        Thread.sleep(milliseconds);

    }

    static EntityLog waitForLogCount(Company company, Entity entity, int expectedCount, EntityService entityService) throws Exception {
        // Looking for the first searchKey to be logged against the entity
        int i = 0;
        int timeout = 100;
        int count = 0;

        while (i <= timeout) {
            Entity updatedEntity = entityService.getEntity(company, entity.getMetaKey());
            count = entityService.getLogCount(company, updatedEntity.getMetaKey());

            EntityLog log = entityService.getLastEntityLog(company, updatedEntity.getMetaKey());
            // We have at least one log?
            if (count == expectedCount)
                return log;
            Thread.yield();
            if (i > 20)
                waitAWhile("Waiting {} seconds for the log to update");
            i++;
        }
        if (i > 22)
            logger.info("Wait for log got to [{}] for entityId [{}]", i,
                    entity.getId());
        throw new Exception(String.format("Timeout waiting for the requested log count of %s. Got to %s", expectedCount, count));
    }
}
