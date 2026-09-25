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

            try
            {
                var installed = Font.GetOSInstalledFontNames();
                string chosen =
                    installed.FirstOrDefault(n => n.IndexOf("Noto Sans Arabic", StringComparison.OrdinalIgnoreCase) >= 0) ??
                    installed.FirstOrDefault(n => n.IndexOf("Noto Naskh Arabic", StringComparison.OrdinalIgnoreCase) >= 0) ??
                    installed.FirstOrDefault(n => n.IndexOf("Arabic", StringComparison.OrdinalIgnoreCase) >= 0) ??
                    installed.FirstOrDefault(n => n.Equals("Tahoma", StringComparison.OrdinalIgnoreCase));

                if (!string.IsNullOrEmpty(chosen))
                    cached = Font.CreateDynamicFontFromOSFont(chosen, 32);
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
