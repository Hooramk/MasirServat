using UnityEngine;

namespace MasirServat
{
    public sealed class ObjectiveBeacon : MonoBehaviour
    {
        public Transform target;
        public float height = 6f;

        GameObject diamond;
        TextMesh label;

        void Start()
        {
            diamond = GameObject.CreatePrimitive(PrimitiveType.Cube);
            diamond.name = "ObjectiveDiamond";
            diamond.transform.SetParent(transform, false);
            diamond.transform.localScale = Vector3.one * 0.7f;
            diamond.transform.rotation = Quaternion.Euler(45,45,45);
            var col=diamond.GetComponent<Collider>();
            if(col!=null) Destroy(col);
            var r=diamond.GetComponent<Renderer>();
            r.material.color=new Color(0.95f,0.72f,0.18f);

            var go=new GameObject("ObjectiveLabel");
            go.transform.SetParent(transform,false);
            go.transform.localPosition=new Vector3(0,1.1f,0);
            label=go.AddComponent<TextMesh>();
            label.font=UIFontProvider.Get();
            label.fontSize=48;
            label.characterSize=0.07f;
            label.anchor=TextAnchor.MiddleCenter;
            label.alignment=TextAlignment.Center;
            label.color=Color.white;
            var mr=go.GetComponent<MeshRenderer>();
            if(mr!=null && label.font!=null) mr.sharedMaterial=label.font.material;
        }

        void Update()
        {
            if(GameState.I==null) return;

            var targetName = TargetNameForStep(GameState.I.Data.questStep);
            var t=GameObject.Find(targetName)?.transform;
            if(t!=null) target=t;

            if(target==null)
            {
                if(diamond!=null) diamond.SetActive(false);
                if(label!=null) label.gameObject.SetActive(false);
                return;
            }

            if(diamond!=null) diamond.SetActive(true);
            if(label!=null) label.gameObject.SetActive(true);

            transform.position=target.position+Vector3.up*height;
            transform.Rotate(0,70f*Time.deltaTime,0,Space.World);

            if(label!=null)
            {
                label.text=PersianText.Fix("هدف");
                var cam=Camera.main;
                if(cam!=null)
                {
                    label.transform.rotation=Quaternion.LookRotation(
                        label.transform.position-cam.transform.position,
                        Vector3.up
                    );
                }
            }

            if(diamond!=null)
                diamond.transform.localPosition=new Vector3(0,Mathf.Sin(Time.time*3f)*0.3f,0);
        }

        static string TargetNameForStep(int step)
        {
            switch(step)
            {
                case 0:return "Cafe";
                case 1:return "University";
                case 2:return "Bank";
                case 3:return "Scooter";
                case 4:return "Business";
                default:return "";
            }
        }
    }
}
