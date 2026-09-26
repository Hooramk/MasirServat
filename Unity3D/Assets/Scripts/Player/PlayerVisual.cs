using UnityEngine;

namespace MasirServat
{
    public sealed class PlayerVisual : MonoBehaviour
    {
        Transform visualRoot;
        Transform leftArm, rightArm, leftLeg, rightLeg;
        CharacterController controller;
        float phase;

        void Awake()
        {
            controller = GetComponent<CharacterController>();
            BuildAvatar();
        }

        void BuildAvatar()
        {
            visualRoot = new GameObject("AvatarVisual").transform;
            visualRoot.SetParent(transform, false);

            // Legs
            leftLeg = Part("LeftLeg", new Vector3(-0.17f, 0.46f, 0), new Vector3(0.22f, 0.72f, 0.26f),
                new Color(0.08f, 0.12f, 0.19f));
            rightLeg = Part("RightLeg", new Vector3(0.17f, 0.46f, 0), new Vector3(0.22f, 0.72f, 0.26f),
                new Color(0.08f, 0.12f, 0.19f));

            // Shoes
            Part("LeftShoe", new Vector3(-0.17f, 0.10f, 0.06f), new Vector3(0.26f, 0.16f, 0.42f),
                new Color(0.05f, 0.05f, 0.06f));
            Part("RightShoe", new Vector3(0.17f, 0.10f, 0.06f), new Vector3(0.26f, 0.16f, 0.42f),
                new Color(0.05f, 0.05f, 0.06f));

            // Torso/jacket
            Part("Torso", new Vector3(0, 1.18f, 0), new Vector3(0.72f, 0.82f, 0.38f),
                new Color(0.06f, 0.18f, 0.30f));
            Part("Shirt", new Vector3(0, 1.23f, 0.205f), new Vector3(0.34f, 0.54f, 0.035f),
                new Color(0.86f, 0.87f, 0.82f));

            // Arms
            leftArm = Part("LeftArm", new Vector3(-0.48f, 1.19f, 0), new Vector3(0.18f, 0.72f, 0.22f),
                new Color(0.07f, 0.17f, 0.28f));
            rightArm = Part("RightArm", new Vector3(0.48f, 1.19f, 0), new Vector3(0.18f, 0.72f, 0.22f),
                new Color(0.07f, 0.17f, 0.28f));

            // Hands
            Sphere("LeftHand", new Vector3(-0.48f, 0.82f, 0), 0.13f, new Color(0.72f, 0.48f, 0.32f));
            Sphere("RightHand", new Vector3(0.48f, 0.82f, 0), 0.13f, new Color(0.72f, 0.48f, 0.32f));

            // Head + hair
            Sphere("Head", new Vector3(0, 1.83f, 0), 0.31f, new Color(0.76f, 0.52f, 0.36f));
            var hair = Sphere("Hair", new Vector3(0, 2.00f, -0.015f), 0.30f, new Color(0.07f, 0.05f, 0.04f));
            hair.localScale = new Vector3(1.0f, 0.48f, 1.03f);

            // Face direction cue.
            Part("Face", new Vector3(0, 1.84f, 0.295f), new Vector3(0.20f, 0.10f, 0.025f),
                new Color(0.30f, 0.18f, 0.12f));
        }

        Transform Part(string name, Vector3 localPos, Vector3 scale, Color color)
        {
            var go = GameObject.CreatePrimitive(PrimitiveType.Cube);
            go.name = name;
            go.transform.SetParent(visualRoot, false);
            go.transform.localPosition = localPos;
            go.transform.localScale = scale;
            var c = go.GetComponent<Collider>();
            if (c != null) Destroy(c);
            SafeMaterial.Apply(go, color);
            return go.transform;
        }

        Transform Sphere(string name, Vector3 localPos, float scale, Color color)
        {
            var go = GameObject.CreatePrimitive(PrimitiveType.Sphere);
            go.name = name;
            go.transform.SetParent(visualRoot, false);
            go.transform.localPosition = localPos;
            go.transform.localScale = Vector3.one * scale;
            var c = go.GetComponent<Collider>();
            if (c != null) Destroy(c);
            SafeMaterial.Apply(go, color);
            return go.transform;
        }

        void Update()
        {
            if (visualRoot == null || controller == null) return;

            Vector3 flat = controller.velocity;
            flat.y = 0;
            float speed01 = Mathf.Clamp01(flat.magnitude / 4.5f);

            if (speed01 > 0.04f)
                phase += Time.deltaTime * Mathf.Lerp(5f, 9f, speed01);

            float swing = Mathf.Sin(phase) * 28f * speed01;
            leftArm.localRotation = Quaternion.Euler(swing, 0, 0);
            rightArm.localRotation = Quaternion.Euler(-swing, 0, 0);
            leftLeg.localRotation = Quaternion.Euler(-swing * 0.75f, 0, 0);
            rightLeg.localRotation = Quaternion.Euler(swing * 0.75f, 0, 0);

            float bob = Mathf.Abs(Mathf.Sin(phase * 2f)) * 0.035f * speed01;
            visualRoot.localPosition = new Vector3(0, bob, 0);
        }
    }
}
