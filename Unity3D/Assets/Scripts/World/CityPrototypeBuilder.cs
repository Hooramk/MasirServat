using System;
using UnityEngine;

namespace MasirServat
{
    public sealed class CityPrototypeBuilder : MonoBehaviour
    {
        static readonly Color RoadColor = new Color(0.16f, 0.18f, 0.20f);
        static readonly Color GrassColor = new Color(0.36f, 0.56f, 0.34f);
        static readonly Color GoldColor = new Color(0.78f, 0.64f, 0.35f);

        public Transform Build()
        {
            // The player is created first so the game remains controllable even if
            // one optional city decoration fails on a specific Android device.
            Transform player = CreatePlayer(new Vector3(-13.8f, 0.05f, -2.5f));

            Safe(CreateGround, "ground");
            Safe(CreateRoads, "roads");
            Safe(CreateDecorativeBlocks, "background");

            Safe(() => CreateBuilding("Home", "خانه", new Vector3(-18, 2.5f, -16), new Vector3(10, 5, 8),
                new Color(0.72f, 0.58f, 0.45f), InteractionType.Home, "ورود به خانه / پایان روز"), "home");

            Safe(() => CreateBuilding("Cafe", "کافه", new Vector3(-18, 2.3f, 3), new Vector3(9, 4.6f, 8),
                new Color(0.42f, 0.22f, 0.16f), InteractionType.CafeJob, "شروع شیفت کافه"), "cafe");

            Safe(() => CreateBuilding("University", "دانشگاه", new Vector3(18, 3.2f, -16), new Vector3(13, 6.4f, 9),
                new Color(0.75f, 0.70f, 0.56f), InteractionType.University, "تمرین مهارت · ۳۵۰هزار"), "university");

            Safe(() => CreateBuilding("Bank", "بانک", new Vector3(2, 2.4f, 17), new Vector3(10, 4.8f, 7),
                new Color(0.27f, 0.48f, 0.47f), InteractionType.Bank, "۲ میلیون پس‌انداز"), "bank");

            Safe(() => CreateBuilding("Shop", "فروشگاه دیجیتال", new Vector3(18, 2.5f, 4), new Vector3(10, 5, 8),
                new Color(0.58f, 0.31f, 0.37f), InteractionType.Shop, "خرید / ارتقای لپ‌تاپ"), "shop");

            Safe(() => CreateBuilding("Gym", "باشگاه", new Vector3(-2, 2.5f, -20), new Vector3(10, 5, 7),
                new Color(0.22f, 0.29f, 0.36f), InteractionType.Gym, "تمرین · ۲۲۰هزار"), "gym");

            Safe(() => CreateBuilding("Business", "ملک خالی", new Vector3(-18, 2.2f, 18), new Vector3(10, 4.4f, 8),
                new Color(0.50f, 0.50f, 0.48f), InteractionType.Business, "راه‌اندازی / ارتقای کسب‌وکار"), "business");

            Safe(() => CreateBuilding("DeliveryHub", "مرکز ارسال", new Vector3(19, 2.1f, 19), new Vector3(9, 4.2f, 7),
                new Color(0.34f, 0.46f, 0.64f), InteractionType.DeliveryJob, "کار پیک · نیاز به موتور"), "delivery");

            Safe(() => CreateScooter(new Vector3(12, 0.55f, 10)), "scooter");

            for (int i = 0; i < 14; i++)
            {
                int index = i;
                Safe(() => CreateTree(new Vector3(
                    UnityEngine.Random.Range(-31f, 31f), 0,
                    UnityEngine.Random.Range(-31f, 31f))), "tree_" + index);
            }

            for (int i = 0; i < 8; i++)
            {
                int index = i;
                Safe(() => CreateNPC(new Vector3(
                    UnityEngine.Random.Range(-20f, 20f), 1f,
                    UnityEngine.Random.Range(-12f, 20f)), index), "npc_" + index);
            }

            return player;
        }

        static void Safe(Action action, string part)
        {
            try
            {
                action();
            }
            catch (Exception ex)
            {
                Debug.LogError("CITY_PART_FAILED: " + part + "\n" + ex);
            }
        }

        static void Tint(GameObject go, Color color)
        {
            try
            {
                SafeMaterial.Apply(go, color);
            }
            catch (Exception ex)
            {
                Debug.LogWarning("Safe material skipped for " + go.name + ": " + ex.Message);
            }
        }

        void CreateGround()
        {
            var ground = GameObject.CreatePrimitive(PrimitiveType.Plane);
            ground.name = "Ground";
            ground.transform.position = Vector3.zero;
            ground.transform.localScale = new Vector3(8, 1, 8);
            Tint(ground, GrassColor);
        }

        void CreateRoads()
        {
            CreateRoad(new Vector3(0, 0.03f, 0), new Vector3(80, 0.05f, 8));
            CreateRoad(new Vector3(0, 0.035f, 0), new Vector3(8, 0.05f, 80));
            CreateRoad(new Vector3(0, 0.04f, -12), new Vector3(58, 0.05f, 5));
            CreateRoad(new Vector3(-10, 0.045f, 14), new Vector3(5, 0.05f, 38));
        }

        void CreateRoad(Vector3 pos, Vector3 scale)
        {
            var road = GameObject.CreatePrimitive(PrimitiveType.Cube);
            road.name = "Road";
            road.transform.position = pos;
            road.transform.localScale = scale;
            Tint(road, RoadColor);
        }

        void CreateDecorativeBlocks()
        {
            Color[] colors =
            {
                new Color(0.46f,0.55f,0.61f),
                new Color(0.62f,0.55f,0.48f),
                new Color(0.48f,0.49f,0.55f)
            };

            for (int i = 0; i < 16; i++)
            {
                float x = i < 8 ? -34 : 34;
                float z = -32 + (i % 8) * 9f;
                float h = 5 + (i % 4) * 2f;

                var b = GameObject.CreatePrimitive(PrimitiveType.Cube);
                b.name = "BackgroundBuilding";
                b.transform.position = new Vector3(x, h / 2f, z);
                b.transform.localScale = new Vector3(7, h, 7);
                Tint(b, colors[i % colors.Length]);
            }
        }

        void CreateBuilding(string name, string label, Vector3 pos, Vector3 scale, Color color,
            InteractionType type, string prompt)
        {
            var building = GameObject.CreatePrimitive(PrimitiveType.Cube);
            building.name = name;
            building.transform.position = pos;
            building.transform.localScale = scale;
            Tint(building, color);

            var roof = GameObject.CreatePrimitive(PrimitiveType.Cube);
            roof.name = name + "_Roof";
            roof.transform.SetParent(building.transform, true);
            roof.transform.position = pos + Vector3.up * (scale.y * 0.56f);
            roof.transform.localScale = new Vector3(1.06f, 0.12f, 1.06f);
            Tint(roof, color * 0.82f);

            // Interaction marker is created before the optional text sign.
            // This keeps gameplay functional even if an Android font is unavailable.
            var marker = GameObject.CreatePrimitive(PrimitiveType.Cylinder);
            marker.name = name + "_Interaction";
            marker.transform.position = pos + new Vector3(0, 0.12f, -scale.z * 0.5f - 1.5f);
            marker.transform.localScale = new Vector3(1.15f, 0.08f, 1.15f);
            Tint(marker, GoldColor);

            var ia = marker.AddComponent<Interactable>();
            ia.type = type;
            ia.title = label;
            ia.prompt = prompt;
            if (type == InteractionType.CafeJob) ia.basePay = 650_000;
            if (type == InteractionType.DeliveryJob) ia.basePay = 1_050_000;

            Safe(() => CreateSign(name + "_Sign", label,
                pos + new Vector3(0, scale.y * 0.15f, -scale.z * 0.52f)), name + "_sign");
        }

        void CreateSign(string name, string label, Vector3 pos)
        {
            var sign = new GameObject(name);
            sign.transform.position = pos;
            var tm = sign.AddComponent<TextMesh>();
            tm.text = PersianText.Fix(label);
            tm.fontSize = 60;
            tm.characterSize = 0.09f;
            tm.anchor = TextAnchor.MiddleCenter;
            tm.alignment = TextAlignment.Center;
            tm.color = Color.white;

            var font = UIFontProvider.Get();
            if (font != null)
            {
                tm.font = font;
                var meshRenderer = sign.GetComponent<MeshRenderer>();
                if (meshRenderer != null && font.material != null)
                    meshRenderer.sharedMaterial = font.material;
            }
        }

        Transform CreatePlayer(Vector3 pos)
        {
            var root = new GameObject("Player");
            root.transform.position = pos;

            var cc = root.AddComponent<CharacterController>();
            cc.height = 1.8f;
            cc.radius = 0.38f;
            cc.center = new Vector3(0, 0.9f, 0);

            var body = GameObject.CreatePrimitive(PrimitiveType.Capsule);
            body.name = "Body";
            body.transform.SetParent(root.transform, false);
            body.transform.localPosition = new Vector3(0, 0.9f, 0);
            body.transform.localScale = new Vector3(0.72f, 0.9f, 0.72f);
            var bodyCollider = body.GetComponent<Collider>();
            if (bodyCollider != null) Destroy(bodyCollider);
            Tint(body, new Color(0.04f, 0.16f, 0.28f));

            var head = GameObject.CreatePrimitive(PrimitiveType.Sphere);
            head.name = "Head";
            head.transform.SetParent(root.transform, false);
            head.transform.localPosition = new Vector3(0, 1.82f, 0);
            head.transform.localScale = Vector3.one * 0.48f;
            var headCollider = head.GetComponent<Collider>();
            if (headCollider != null) Destroy(headCollider);
            Tint(head, new Color(0.76f, 0.55f, 0.40f));

            root.AddComponent<PlayerMotor>();
            return root.transform;
        }

        void CreateScooter(Vector3 pos)
        {
            var root = new GameObject("Scooter");
            root.transform.position = pos;

            var body = GameObject.CreatePrimitive(PrimitiveType.Cube);
            body.name = "ScooterBody";
            body.transform.SetParent(root.transform, false);
            body.transform.localPosition = new Vector3(0, 0.15f, 0);
            body.transform.localScale = new Vector3(0.55f, 0.18f, 1.45f);
            Tint(body, new Color(0.42f, 0.44f, 0.46f));
            Destroy(body.GetComponent<Collider>());

            CreateWheel(root.transform, new Vector3(0, -0.05f, 0.48f));
            CreateWheel(root.transform, new Vector3(0, -0.05f, -0.48f));

            var handle = GameObject.CreatePrimitive(PrimitiveType.Cube);
            handle.name = "ScooterHandle";
            handle.transform.SetParent(root.transform, false);
            handle.transform.localPosition = new Vector3(0, 0.58f, 0.42f);
            handle.transform.localScale = new Vector3(0.72f, 0.08f, 0.08f);
            Tint(handle, new Color(0.20f, 0.22f, 0.24f));
            Destroy(handle.GetComponent<Collider>());

            var collider = root.AddComponent<BoxCollider>();
            collider.size = new Vector3(1.1f, 1.1f, 2.0f);
            collider.center = new Vector3(0, 0.3f, 0);

            var mount = root.AddComponent<VehicleMount>();
            var ia = root.AddComponent<Interactable>();
            ia.type = InteractionType.Vehicle;
            ia.title = "موتور";
            ia.prompt = "موتور · خرید ۹ میلیون / سوار شدن";
            ia.vehicle = mount;
        }

        void CreateWheel(Transform parent, Vector3 local)
        {
            var wheel = GameObject.CreatePrimitive(PrimitiveType.Cylinder);
            wheel.name = "Wheel";
            wheel.transform.SetParent(parent, false);
            wheel.transform.localPosition = local;
            wheel.transform.localRotation = Quaternion.Euler(0, 0, 90);
            wheel.transform.localScale = new Vector3(0.28f, 0.12f, 0.28f);
            Tint(wheel, new Color(0.04f, 0.04f, 0.04f));
            Destroy(wheel.GetComponent<Collider>());
        }

        void CreateTree(Vector3 pos)
        {
            if (Mathf.Abs(pos.x) < 6 || Mathf.Abs(pos.z) < 6) return;

            var trunk = GameObject.CreatePrimitive(PrimitiveType.Cylinder);
            trunk.name = "TreeTrunk";
            trunk.transform.position = pos + Vector3.up * 0.8f;
            trunk.transform.localScale = new Vector3(0.22f, 0.8f, 0.22f);
            Tint(trunk, new Color(0.34f, 0.23f, 0.15f));

            var crown = GameObject.CreatePrimitive(PrimitiveType.Sphere);
            crown.name = "TreeCrown";
            crown.transform.position = pos + Vector3.up * 2.1f;
            crown.transform.localScale = new Vector3(1.5f, 1.8f, 1.5f);
            Tint(crown, new Color(0.20f, 0.48f, 0.24f));
            Destroy(crown.GetComponent<Collider>());
        }

        void CreateNPC(Vector3 pos, int index)
        {
            var npc = GameObject.CreatePrimitive(PrimitiveType.Capsule);
            npc.name = "NPC_" + index;
            npc.transform.position = pos;
            npc.transform.localScale = new Vector3(0.7f, 0.9f, 0.7f);
            Tint(npc, Color.HSVToRGB((index * 0.13f) % 1f, 0.42f, 0.78f));
            npc.AddComponent<NPCWander>();

            var ia = npc.AddComponent<Interactable>();
            ia.type = InteractionType.NPC;
            ia.title = "شهروند";
            ia.prompt = "صحبت کوتاه";
        }
    }
}
