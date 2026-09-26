using System;
using UnityEngine;

namespace MasirServat
{
    public sealed class CityPrototypeBuilder : MonoBehaviour
    {
        static readonly Color Grass = new Color(0.27f, 0.46f, 0.26f);
        static readonly Color Road = new Color(0.13f, 0.15f, 0.18f);
        static readonly Color Sidewalk = new Color(0.63f, 0.62f, 0.58f);
        static readonly Color Gold = new Color(0.92f, 0.65f, 0.24f);
        static readonly Color Window = new Color(0.20f, 0.48f, 0.67f);
        static readonly Color Lamp = new Color(1f, 0.78f, 0.36f);

        public Transform Build()
        {
            Transform player = CreatePlayer(new Vector3(-12.5f, 0.05f, -3.8f));

            Safe(CreateGround, "ground");
            Safe(CreateRoadNetwork, "roads");
            Safe(CreatePark, "park");
            Safe(CreateSkyDetails, "sky");

            Safe(() => CreateBuilding("Cafe", "کافه", new Vector3(-18, 2.7f, 5.5f), new Vector3(10, 5.4f, 8),
                new Color(0.48f, 0.24f, 0.16f), InteractionType.CafeJob, "شروع شیفت کافه", true), "cafe");

            Safe(() => CreateBuilding("Home", "خانه", new Vector3(-18, 3.2f, -17), new Vector3(11, 6.4f, 9),
                new Color(0.67f, 0.52f, 0.40f), InteractionType.Home, "ورود به خانه / پایان روز", false), "home");

            Safe(() => CreateBuilding("University", "دانشگاه", new Vector3(17, 4.0f, -17), new Vector3(15, 8, 10),
                new Color(0.74f, 0.68f, 0.51f), InteractionType.University, "تمرین مهارت · ۳۵۰هزار", false), "university");

            Safe(() => CreateBuilding("Bank", "بانک", new Vector3(4, 2.8f, 18), new Vector3(11, 5.6f, 8),
                new Color(0.22f, 0.47f, 0.43f), InteractionType.Bank, "۲ میلیون پس‌انداز", false), "bank");

            Safe(() => CreateBuilding("Shop", "فروشگاه دیجیتال", new Vector3(18, 2.9f, 5.5f), new Vector3(11, 5.8f, 8),
                new Color(0.56f, 0.27f, 0.36f), InteractionType.Shop, "خرید / ارتقای لپ‌تاپ", true), "shop");

            Safe(() => CreateBuilding("Gym", "باشگاه", new Vector3(1, 3.0f, -20), new Vector3(11, 6, 8),
                new Color(0.21f, 0.27f, 0.34f), InteractionType.Gym, "تمرین · ۲۲۰هزار", false), "gym");

            Safe(() => CreateBuilding("Business", "ملک خالی", new Vector3(-19, 2.5f, 20), new Vector3(10, 5, 8),
                new Color(0.48f, 0.47f, 0.43f), InteractionType.Business, "راه‌اندازی / ارتقای کسب‌وکار", false), "business");

            Safe(() => CreateBuilding("DeliveryHub", "مرکز ارسال", new Vector3(19, 2.5f, 20), new Vector3(10, 5, 8),
                new Color(0.29f, 0.43f, 0.62f), InteractionType.DeliveryJob, "کار پیک · نیاز به موتور", false), "delivery");

            Safe(CreateStreetFurniture, "street_furniture");
            Safe(CreateTrafficDetails, "traffic");
            Safe(() => CreateScooter(new Vector3(10.5f, 0.55f, 9.0f)), "scooter");
            Safe(CreateCitizens, "citizens");
            Safe(CreateSkyline, "skyline");

            return player;
        }

        static void Safe(Action action, string part)
        {
            try { action(); }
            catch (Exception ex) { Debug.LogError("CITY_PART_FAILED: " + part + "\n" + ex); }
        }

        GameObject Box(string name, Vector3 pos, Vector3 scale, Color color, Transform parent = null)
        {
            var go = GameObject.CreatePrimitive(PrimitiveType.Cube);
            go.name = name;
            if (parent != null) go.transform.SetParent(parent, true);
            go.transform.position = pos;
            go.transform.localScale = scale;
            SafeMaterial.Apply(go, color);
            return go;
        }

        GameObject Cylinder(string name, Vector3 pos, Vector3 scale, Color color, Transform parent = null)
        {
            var go = GameObject.CreatePrimitive(PrimitiveType.Cylinder);
            go.name = name;
            if (parent != null) go.transform.SetParent(parent, true);
            go.transform.position = pos;
            go.transform.localScale = scale;
            SafeMaterial.Apply(go, color);
            return go;
        }

        GameObject Sphere(string name, Vector3 pos, Vector3 scale, Color color, Transform parent = null)
        {
            var go = GameObject.CreatePrimitive(PrimitiveType.Sphere);
            go.name = name;
            if (parent != null) go.transform.SetParent(parent, true);
            go.transform.position = pos;
            go.transform.localScale = scale;
            SafeMaterial.Apply(go, color);
            return go;
        }

        void RemoveCollider(GameObject go)
        {
            var c = go.GetComponent<Collider>();
            if (c != null) Destroy(c);
        }

        void CreateGround()
        {
            Box("Ground", new Vector3(0, -0.36f, 0), new Vector3(86, 0.7f, 86), Grass);
        }

        void CreateRoadNetwork()
        {
            Box("RoadEW", new Vector3(0, 0.02f, 0), new Vector3(86, 0.10f, 9), Road);
            Box("RoadNS", new Vector3(0, 0.025f, 0), new Vector3(9, 0.10f, 86), Road);
            Box("RoadWest", new Vector3(-14, 0.03f, 2), new Vector3(5.5f, 0.10f, 58), Road);
            Box("RoadSouth", new Vector3(2, 0.035f, -13), new Vector3(58, 0.10f, 5.5f), Road);

            // Sidewalk strips.
            Box("Walk_N1", new Vector3(0, 0.10f, 5.3f), new Vector3(86, 0.18f, 1.6f), Sidewalk);
            Box("Walk_S1", new Vector3(0, 0.10f, -5.3f), new Vector3(86, 0.18f, 1.6f), Sidewalk);
            Box("Walk_E1", new Vector3(5.3f, 0.11f, 0), new Vector3(1.6f, 0.18f, 86), Sidewalk);
            Box("Walk_W1", new Vector3(-5.3f, 0.11f, 0), new Vector3(1.6f, 0.18f, 86), Sidewalk);

            CreateCrosswalk(new Vector3(-14, 0.09f, 0), true);
            CreateCrosswalk(new Vector3(0, 0.09f, -13), false);
        }

        void CreateCrosswalk(Vector3 center, bool eastWest)
        {
            for (int i = -3; i <= 3; i++)
            {
                Vector3 p = center + (eastWest ? new Vector3(i * 0.95f, 0, 0) : new Vector3(0, 0, i * 0.95f));
                Vector3 s = eastWest ? new Vector3(0.55f, 0.035f, 4.6f) : new Vector3(4.6f, 0.035f, 0.55f);
                Box("Crosswalk", p, s, new Color(0.88f, 0.88f, 0.84f));
            }
        }

        void CreatePark()
        {
            Box("ParkPad", new Vector3(11.5f, 0.03f, -3.0f), new Vector3(11, 0.12f, 9), new Color(0.33f, 0.55f, 0.30f));
            Box("ParkPath", new Vector3(11.5f, 0.11f, -3.0f), new Vector3(2.0f, 0.10f, 8.5f), new Color(0.68f, 0.61f, 0.49f));

            CreateTree(new Vector3(8, 0, -5.6f), 1.05f);
            CreateTree(new Vector3(14.7f, 0, -5.4f), 1.10f);
            CreateTree(new Vector3(8.5f, 0, -0.2f), 0.95f);
            CreateTree(new Vector3(14.5f, 0, -0.5f), 1.00f);
            CreateBench(new Vector3(9.8f, 0.2f, -3.4f), 90);
            CreateBench(new Vector3(13.2f, 0.2f, -2.3f), -90);
        }

        void CreateSkyDetails()
        {
            var sun = Sphere("SunDisc", new Vector3(-32, 25, 48), new Vector3(5, 5, 5), new Color(1f, 0.73f, 0.30f));
            RemoveCollider(sun);

            // Soft chunky clouds give the skyline depth without textures.
            for (int i = 0; i < 7; i++)
            {
                float x = -30 + i * 10f;
                var cloud = Sphere("Cloud", new Vector3(x, 18 + (i % 2) * 2, 35 + (i % 3) * 4),
                    new Vector3(5.2f, 1.5f, 2.2f), new Color(0.92f, 0.95f, 0.98f));
                RemoveCollider(cloud);
            }
        }

        void CreateBuilding(string name, string label, Vector3 pos, Vector3 scale, Color color,
            InteractionType type, string prompt, bool awning)
        {
            var root = new GameObject(name);
            root.transform.position = pos;

            var body = Box(name + "_Body", pos, scale, color, root.transform);

            Box(name + "_Roof", pos + Vector3.up * (scale.y * 0.55f),
                new Vector3(scale.x * 1.05f, 0.35f, scale.z * 1.05f), color * 0.82f, root.transform);

            // Entrance.
            Box(name + "_Door", pos + new Vector3(0, -scale.y * 0.16f, -scale.z * 0.505f),
                new Vector3(1.5f, 2.2f, 0.16f), new Color(0.15f, 0.16f, 0.18f), root.transform);

            CreateWindowGrid(root.transform, pos, scale);

            if (awning)
            {
                Box(name + "_Awning", pos + new Vector3(0, -scale.y * 0.02f, -scale.z * 0.58f),
                    new Vector3(scale.x * 0.72f, 0.32f, 1.35f), Gold, root.transform);
            }

            // Small rooftop feature for silhouette.
            Box(name + "_RoofRoom", pos + new Vector3(scale.x * 0.20f, scale.y * 0.67f, 0),
                new Vector3(scale.x * 0.25f, 1.25f, scale.z * 0.32f), color * 0.72f, root.transform);

            var marker = Cylinder(name + "_Interaction",
                pos + new Vector3(0, 0.14f, -scale.z * 0.5f - 1.7f),
                new Vector3(1.35f, 0.07f, 1.35f), Gold);
            var ia = marker.AddComponent<Interactable>();
            ia.type = type;
            ia.title = label;
            ia.prompt = prompt;
            if (type == InteractionType.CafeJob) ia.basePay = 650_000;
            if (type == InteractionType.DeliveryJob) ia.basePay = 1_050_000;

            Safe(() => CreateSign(label, pos + new Vector3(0, scale.y * 0.12f, -scale.z * 0.54f)), name + "_sign");
        }

        void CreateWindowGrid(Transform parent, Vector3 pos, Vector3 scale)
        {
            int cols = Mathf.Clamp(Mathf.RoundToInt(scale.x / 3f), 2, 5);
            int rows = Mathf.Clamp(Mathf.RoundToInt(scale.y / 2.6f), 1, 3);

            for (int r = 0; r < rows; r++)
            {
                for (int c = 0; c < cols; c++)
                {
                    float x = Mathf.Lerp(-scale.x * 0.34f, scale.x * 0.34f, cols == 1 ? 0.5f : c / (float)(cols - 1));
                    float y = -scale.y * 0.18f + r * 1.65f + 1.1f;
                    if (r == 0 && Mathf.Abs(x) < 1.1f) continue;

                    var w = Box("Window", pos + new Vector3(x, y, -scale.z * 0.507f),
                        new Vector3(1.25f, 0.95f, 0.10f),
                        (c + r) % 3 == 0 ? new Color(0.91f, 0.69f, 0.30f) : Window,
                        parent);
                    RemoveCollider(w);
                }
            }
        }

        void CreateSign(string label, Vector3 pos)
        {
            var sign = new GameObject("Sign_" + label);
            sign.transform.position = pos;

            var tm = sign.AddComponent<TextMesh>();
            tm.text = PersianText.Fix(label);
            tm.fontSize = 70;
            tm.characterSize = 0.075f;
            tm.anchor = TextAnchor.MiddleCenter;
            tm.alignment = TextAlignment.Center;
            tm.color = Color.white;

            var font = UIFontProvider.Get();
            if (font != null)
            {
                tm.font = font;
                var mr = sign.GetComponent<MeshRenderer>();
                if (mr != null && font.material != null) mr.sharedMaterial = font.material;
            }
        }

        Transform CreatePlayer(Vector3 pos)
        {
            var root = new GameObject("Player");
            root.transform.position = pos;

            var cc = root.AddComponent<CharacterController>();
            cc.height = 2.08f;
            cc.radius = 0.34f;
            cc.center = new Vector3(0, 1.04f, 0);

            root.AddComponent<PlayerMotor>();
            root.AddComponent<PlayerVisual>();
            return root.transform;
        }

        void CreateStreetFurniture()
        {
            Vector3[] lights =
            {
                new Vector3(-8,0,6), new Vector3(8,0,6), new Vector3(-8,0,-6), new Vector3(8,0,-6),
                new Vector3(-20,0,-6), new Vector3(20,0,-6), new Vector3(-20,0,6), new Vector3(20,0,6),
                new Vector3(-6,0,18), new Vector3(6,0,18)
            };
            foreach (var p in lights) CreateStreetLight(p);

            CreateBench(new Vector3(-7.2f, 0.2f, 10.5f), 0);
            CreateBench(new Vector3(7.2f, 0.2f, 10.5f), 180);

            Vector3[] trees =
            {
                new Vector3(-28,0,-7), new Vector3(-26,0,12), new Vector3(-9,0,15),
                new Vector3(10,0,14), new Vector3(29,0,-7), new Vector3(28,0,12),
                new Vector3(-28,0,-28), new Vector3(28,0,-28), new Vector3(-8,0,29), new Vector3(10,0,29)
            };
            for (int i = 0; i < trees.Length; i++) CreateTree(trees[i], 0.9f + (i % 3) * 0.12f);
        }

        void CreateStreetLight(Vector3 pos)
        {
            var pole = Cylinder("LampPole", pos + Vector3.up * 2.3f,
                new Vector3(0.10f, 2.3f, 0.10f), new Color(0.14f,0.15f,0.17f));
            RemoveCollider(pole);

            var bulb = Sphere("LampGlow", pos + Vector3.up * 4.6f,
                new Vector3(0.34f,0.34f,0.34f), Lamp);
            RemoveCollider(bulb);
        }

        void CreateBench(Vector3 pos, float yaw)
        {
            var root = new GameObject("Bench");
            root.transform.position = pos;
            root.transform.rotation = Quaternion.Euler(0, yaw, 0);

            var seat = Box("Seat", pos + Vector3.up * 0.42f, new Vector3(2.1f,0.18f,0.62f),
                new Color(0.39f,0.23f,0.14f), root.transform);
            seat.transform.localPosition = new Vector3(0,0.42f,0);

            var back = Box("Back", pos + new Vector3(0,0.90f,0.28f), new Vector3(2.1f,0.70f,0.16f),
                new Color(0.39f,0.23f,0.14f), root.transform);
            back.transform.localPosition = new Vector3(0,0.90f,0.28f);
        }

        void CreateTree(Vector3 pos, float scale)
        {
            var trunk = Cylinder("TreeTrunk", pos + Vector3.up * (0.85f * scale),
                new Vector3(0.20f * scale, 0.85f * scale, 0.20f * scale),
                new Color(0.34f, 0.22f, 0.13f));
            RemoveCollider(trunk);

            var crown = Sphere("TreeCrown", pos + Vector3.up * (2.15f * scale),
                new Vector3(1.35f * scale, 1.55f * scale, 1.35f * scale),
                new Color(0.18f + (scale-0.8f)*0.05f, 0.43f, 0.22f));
            RemoveCollider(crown);

            var crown2 = Sphere("TreeCrown2", pos + new Vector3(0.65f*scale, 2.0f*scale, 0.20f),
                new Vector3(0.85f*scale, 0.95f*scale, 0.85f*scale),
                new Color(0.22f,0.50f,0.25f));
            RemoveCollider(crown2);
        }

        void CreateTrafficDetails()
        {
            CreateParkedCar(new Vector3(-2.6f, 0.45f, 9.5f), 90, new Color(0.72f,0.17f,0.14f));
            CreateParkedCar(new Vector3(2.7f, 0.45f, -9.2f), -90, new Color(0.12f,0.33f,0.62f));
            CreateParkedCar(new Vector3(-20.5f, 0.45f, -1.8f), 0, new Color(0.81f,0.68f,0.25f));
            CreateParkedCar(new Vector3(20.5f, 0.45f, 1.8f), 180, new Color(0.38f,0.38f,0.41f));
        }

        void CreateParkedCar(Vector3 pos, float yaw, Color color)
        {
            var root = new GameObject("ParkedCar");
            root.transform.position = pos;
            root.transform.rotation = Quaternion.Euler(0, yaw, 0);

            var body = Box("CarBody", pos, new Vector3(1.8f,0.62f,3.6f), color, root.transform);
            body.transform.localPosition = Vector3.zero;
            var cabin = Box("Cabin", pos + Vector3.up*0.58f, new Vector3(1.5f,0.72f,1.9f),
                new Color(0.20f,0.32f,0.42f), root.transform);
            cabin.transform.localPosition = new Vector3(0,0.58f,0);

            for (int i = 0; i < 4; i++)
            {
                float x = i < 2 ? -0.92f : 0.92f;
                float z = i % 2 == 0 ? -1.15f : 1.15f;
                var wheel = Cylinder("CarWheel", pos, new Vector3(0.34f,0.16f,0.34f),
                    new Color(0.04f,0.04f,0.05f), root.transform);
                wheel.transform.localPosition = new Vector3(x,-0.22f,z);
                wheel.transform.localRotation = Quaternion.Euler(0,0,90);
                RemoveCollider(wheel);
            }
        }

        void CreateScooter(Vector3 pos)
        {
            var root = new GameObject("Scooter");
            root.transform.position = pos;

            var body = Box("ScooterBody", pos + Vector3.up*0.18f,
                new Vector3(0.55f,0.20f,1.55f), new Color(0.36f,0.38f,0.42f), root.transform);
            body.transform.localPosition = new Vector3(0,0.18f,0);
            RemoveCollider(body);

            var handle = Box("ScooterHandle", pos + new Vector3(0,0.72f,0.45f),
                new Vector3(0.82f,0.08f,0.08f), new Color(0.15f,0.16f,0.18f), root.transform);
            handle.transform.localPosition = new Vector3(0,0.72f,0.45f);
            RemoveCollider(handle);

            for (int i = 0; i < 2; i++)
            {
                var wheel = Cylinder("Wheel", pos, new Vector3(0.28f,0.12f,0.28f),
                    new Color(0.04f,0.04f,0.05f), root.transform);
                wheel.transform.localPosition = new Vector3(0,-0.05f,i==0 ? -0.52f : 0.52f);
                wheel.transform.localRotation = Quaternion.Euler(0,0,90);
                RemoveCollider(wheel);
            }

            var collider = root.AddComponent<BoxCollider>();
            collider.size = new Vector3(1.15f,1.25f,2.1f);
            collider.center = new Vector3(0,0.35f,0);

            var mount = root.AddComponent<VehicleMount>();
            var ia = root.AddComponent<Interactable>();
            ia.type = InteractionType.Vehicle;
            ia.title = "موتور";
            ia.prompt = "موتور · خرید ۹ میلیون / سوار شدن";
            ia.vehicle = mount;
        }

        void CreateCitizens()
        {
            Vector3[] positions =
            {
                new Vector3(-9,0.05f,8), new Vector3(-3,0.05f,11), new Vector3(7,0.05f,8),
                new Vector3(12,0.05f,3), new Vector3(-10,0.05f,-9), new Vector3(6,0.05f,-10),
                new Vector3(-22,0.05f,1), new Vector3(22,0.05f,-2)
            };

            for (int i = 0; i < positions.Length; i++)
                CreateCitizen(positions[i], i);
        }

        void CreateCitizen(Vector3 pos, int index)
        {
            var root = new GameObject("NPC_" + index);
            root.transform.position = pos;

            var collider = root.AddComponent<CapsuleCollider>();
            collider.height = 1.8f;
            collider.radius = 0.32f;
            collider.center = new Vector3(0,0.9f,0);

            Color shirt = Color.HSVToRGB((index * 0.14f) % 1f, 0.42f, 0.72f);
            var torso = Box("NPC_Torso", pos, new Vector3(0.58f,0.82f,0.34f), shirt, root.transform);
            torso.transform.localPosition = new Vector3(0,1.12f,0);
            RemoveCollider(torso);

            var head = Sphere("NPC_Head", pos, Vector3.one*0.27f,
                new Color(0.70f,0.47f,0.32f), root.transform);
            head.transform.localPosition = new Vector3(0,1.78f,0);
            RemoveCollider(head);

            var legs = Box("NPC_Legs", pos, new Vector3(0.42f,0.75f,0.28f),
                new Color(0.10f,0.12f,0.16f), root.transform);
            legs.transform.localPosition = new Vector3(0,0.45f,0);
            RemoveCollider(legs);

            root.AddComponent<NPCWander>();
            var ia = root.AddComponent<Interactable>();
            ia.type = InteractionType.NPC;
            ia.title = "شهروند";
            ia.prompt = "گفت‌وگوی کوتاه";
        }

        void CreateSkyline()
        {
            Color[] palette =
            {
                new Color(0.42f,0.48f,0.55f),
                new Color(0.56f,0.51f,0.45f),
                new Color(0.36f,0.41f,0.49f),
                new Color(0.50f,0.45f,0.52f)
            };

            for (int i = 0; i < 18; i++)
            {
                float x = -40 + i * 4.7f;
                float h = 7 + (i % 5) * 2.1f;
                Box("Skyline", new Vector3(x,h/2f,39), new Vector3(4.2f,h,5.2f), palette[i%palette.Length]);
            }

            for (int i = 0; i < 12; i++)
            {
                float z = -34 + i * 6.2f;
                float h = 6 + (i % 4) * 2.4f;
                Box("SkylineSide", new Vector3(40,h/2f,z), new Vector3(5.0f,h,5.2f), palette[(i+1)%palette.Length]);
            }
        }
    }
}
