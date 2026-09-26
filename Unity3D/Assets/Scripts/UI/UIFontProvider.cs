using System;
using System.Linq;
using UnityEngine;

namespace MasirServat
{
    public static class UIFontProvider
    {
        static Font cached;

        public static Font Get()
        {
            if (cached != null) return cached;

            string[] preferred =
            {
                "Noto Sans Arabic",
                "Noto Naskh Arabic",
                "Droid Arabic Naskh",
                "Droid Sans Arabic",
                "Tahoma",
                "Arial",
                "sans-serif"
            };

            try
            {
                var installed = Font.GetOSInstalledFontNames();
                string chosen = preferred
                    .FirstOrDefault(p => installed.Any(n =>
                        n.IndexOf(p, StringComparison.OrdinalIgnoreCase) >= 0));

                if (!string.IsNullOrEmpty(chosen))
                    cached = Font.CreateDynamicFontFromOSFont(chosen, 32);

                if (cached == null)
                    cached = Font.CreateDynamicFontFromOSFont(preferred, 32);
            }
            catch
            {
                cached = null;
            }

            if (cached == null)
                cached = Resources.GetBuiltinResource<Font>("LegacyRuntime.ttf");

            return cached;
        }
    }
}
