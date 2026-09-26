using System;
using System.Collections.Generic;
using System.Text;

namespace MasirServat
{
    public static class PersianText
    {
        struct Forms
        {
            public char iso, fin, init, med;
            public bool joinPrev, joinNext;

            public Forms(int i, int f, int n, int m, bool prev = true, bool next = true)
            {
                iso=(char)i; fin=(char)f; init=(char)n; med=(char)m;
                joinPrev=prev; joinNext=next;
            }
        }

        static readonly Dictionary<char, Forms> Map = new Dictionary<char, Forms>
        {
            ['ء']=new Forms(0xFE80,0xFE80,0xFE80,0xFE80,false,false),
            ['آ']=new Forms(0xFE81,0xFE82,0xFE81,0xFE82,true,false),
            ['أ']=new Forms(0xFE83,0xFE84,0xFE83,0xFE84,true,false),
            ['ؤ']=new Forms(0xFE85,0xFE86,0xFE85,0xFE86,true,false),
            ['إ']=new Forms(0xFE87,0xFE88,0xFE87,0xFE88,true,false),
            ['ئ']=new Forms(0xFE89,0xFE8A,0xFE8B,0xFE8C,true,true),
            ['ا']=new Forms(0xFE8D,0xFE8E,0xFE8D,0xFE8E,true,false),
            ['ب']=new Forms(0xFE8F,0xFE90,0xFE91,0xFE92,true,true),
            ['ة']=new Forms(0xFE93,0xFE94,0xFE93,0xFE94,true,false),
            ['ت']=new Forms(0xFE95,0xFE96,0xFE97,0xFE98,true,true),
            ['ث']=new Forms(0xFE99,0xFE9A,0xFE9B,0xFE9C,true,true),
            ['ج']=new Forms(0xFE9D,0xFE9E,0xFE9F,0xFEA0,true,true),
            ['ح']=new Forms(0xFEA1,0xFEA2,0xFEA3,0xFEA4,true,true),
            ['خ']=new Forms(0xFEA5,0xFEA6,0xFEA7,0xFEA8,true,true),
            ['د']=new Forms(0xFEA9,0xFEAA,0xFEA9,0xFEAA,true,false),
            ['ذ']=new Forms(0xFEAB,0xFEAC,0xFEAB,0xFEAC,true,false),
            ['ر']=new Forms(0xFEAD,0xFEAE,0xFEAD,0xFEAE,true,false),
            ['ز']=new Forms(0xFEAF,0xFEB0,0xFEAF,0xFEB0,true,false),
            ['س']=new Forms(0xFEB1,0xFEB2,0xFEB3,0xFEB4,true,true),
            ['ش']=new Forms(0xFEB5,0xFEB6,0xFEB7,0xFEB8,true,true),
            ['ص']=new Forms(0xFEB9,0xFEBA,0xFEBB,0xFEBC,true,true),
            ['ض']=new Forms(0xFEBD,0xFEBE,0xFEBF,0xFEC0,true,true),
            ['ط']=new Forms(0xFEC1,0xFEC2,0xFEC3,0xFEC4,true,true),
            ['ظ']=new Forms(0xFEC5,0xFEC6,0xFEC7,0xFEC8,true,true),
            ['ع']=new Forms(0xFEC9,0xFECA,0xFECB,0xFECC,true,true),
            ['غ']=new Forms(0xFECD,0xFECE,0xFECF,0xFED0,true,true),
            ['ف']=new Forms(0xFED1,0xFED2,0xFED3,0xFED4,true,true),
            ['ق']=new Forms(0xFED5,0xFED6,0xFED7,0xFED8,true,true),
            ['ك']=new Forms(0xFED9,0xFEDA,0xFEDB,0xFEDC,true,true),
            ['ل']=new Forms(0xFEDD,0xFEDE,0xFEDF,0xFEE0,true,true),
            ['م']=new Forms(0xFEE1,0xFEE2,0xFEE3,0xFEE4,true,true),
            ['ن']=new Forms(0xFEE5,0xFEE6,0xFEE7,0xFEE8,true,true),
            ['ه']=new Forms(0xFEE9,0xFEEA,0xFEEB,0xFEEC,true,true),
            ['و']=new Forms(0xFEED,0xFEEE,0xFEED,0xFEEE,true,false),
            ['ى']=new Forms(0xFEEF,0xFEF0,0xFEEF,0xFEF0,true,false),
            ['ي']=new Forms(0xFEF1,0xFEF2,0xFEF3,0xFEF4,true,true),

            ['پ']=new Forms(0xFB56,0xFB57,0xFB58,0xFB59,true,true),
            ['چ']=new Forms(0xFB7A,0xFB7B,0xFB7C,0xFB7D,true,true),
            ['ژ']=new Forms(0xFB8A,0xFB8B,0xFB8A,0xFB8B,true,false),
            ['ک']=new Forms(0xFB8E,0xFB8F,0xFB90,0xFB91,true,true),
            ['گ']=new Forms(0xFB92,0xFB93,0xFB94,0xFB95,true,true),
            ['ی']=new Forms(0xFBFC,0xFBFD,0xFBFE,0xFBFF,true,true)
        };

        static readonly HashSet<char> Marks = new HashSet<char>
        {
            '\u064B','\u064C','\u064D','\u064E','\u064F','\u0650','\u0651','\u0652','\u0670'
        };

        static bool IsArabic(char c) => Map.ContainsKey(c);
        static bool IsMark(char c) => Marks.Contains(c);

        static int PrevLetter(char[] chars, int i)
        {
            for (int p=i-1;p>=0;p--)
            {
                if (IsMark(chars[p])) continue;
                return p;
            }
            return -1;
        }

        static int NextLetter(char[] chars, int i)
        {
            for (int n=i+1;n<chars.Length;n++)
            {
                if (IsMark(chars[n])) continue;
                return n;
            }
            return -1;
        }

        static string ShapeLine(string line)
        {
            if (string.IsNullOrEmpty(line)) return line;

            var chars=line.ToCharArray();
            var shaped=new char[chars.Length];

            for(int i=0;i<chars.Length;i++)
            {
                char c=chars[i];
                if(!Map.TryGetValue(c,out var f))
                {
                    shaped[i]=c;
                    continue;
                }

                int p=PrevLetter(chars,i);
                int n=NextLetter(chars,i);

                bool connectPrev=false;
                bool connectNext=false;

                if(p>=0 && Map.TryGetValue(chars[p],out var pf))
                    connectPrev = f.joinPrev && pf.joinNext;

                if(n>=0 && Map.TryGetValue(chars[n],out var nf))
                    connectNext = f.joinNext && nf.joinPrev;

                shaped[i] = connectPrev && connectNext ? f.med
                          : connectPrev ? f.fin
                          : connectNext ? f.init
                          : f.iso;
            }

            var units=new List<string>();
            var ltr=new StringBuilder();

            Action flush=()=>{
                if(ltr.Length>0)
                {
                    units.Add(ltr.ToString());
                    ltr.Clear();
                }
            };

            for(int i=0;i<shaped.Length;i++)
            {
                char original=chars[i];
                char visual=shaped[i];

                if(IsArabic(original) || IsMark(original))
                {
                    flush();
                    units.Add(visual.ToString());
                }
                else
                {
                    // Keep western numbers / Latin tokens in their own natural order.
                    ltr.Append(visual);
                }
            }
            flush();

            units.Reverse();
            return string.Concat(units);
        }

        public static string Fix(string text)
        {
            if(string.IsNullOrEmpty(text)) return text ?? "";
            if(!ContainsArabic(text)) return text;

            string normalized=text
                .Replace('ۀ','ه')
                .Replace('ة','ه')
                .Replace('ك','ک')
                .Replace('ي','ی');

            string[] lines=normalized.Replace("\r\n","\n").Split('\n');
            for(int i=0;i<lines.Length;i++)
                lines[i]=ShapeLine(lines[i]);

            return string.Join("\n",lines);
        }

        public static bool ContainsArabic(string text)
        {
            if(string.IsNullOrEmpty(text)) return false;
            foreach(char c in text)
                if((c>='\u0600'&&c<='\u06FF') || (c>='\u0750'&&c<='\u077F'))
                    return true;
            return false;
        }
    }
}
