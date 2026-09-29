using UnityEngine;

namespace MasirServat
{
    public static class SafeMaterial
    {
        static Shader cachedShader;

        static Shader GetShader()
        {
            if (cachedShader != null) return cachedShader;

            cachedShader = Resources.Load<Shader>("MobileUnlitColor");
            if (cachedShader == null)
                cachedShader = Shader.Find("MasirServat/MobileUnlitColor");

            return cachedShader;
        }

        public static Material Create(Color color)
        {
            var shader = GetShader();
            if (shader == null)
            {
                Debug.LogError("SAFE_SHADER_MISSING");
                return null;
            }

            var mat = new Material(shader);
            mat.name = "MS_Unlit_" + ColorUtility.ToHtmlStringRGB(color);
            mat.SetColor("_Color", color);
            return mat;
        }

        public static void Apply(GameObject go, Color color)
        {
            if (go == null) return;
            var renderer = go.GetComponent<Renderer>();
            if (renderer == null) return;

            var mat = Create(color);
            if (mat != null)
                renderer.sharedMaterial = mat;
        }

        public static void Apply(Renderer renderer, Color color)
        {
            if (renderer == null) return;

            var mat = Create(color);
            if (mat != null)
                renderer.sharedMaterial = mat;
        }
    }
}
