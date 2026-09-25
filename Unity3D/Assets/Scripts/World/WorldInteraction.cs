using UnityEngine;

namespace MasirServat
{
    public sealed class WorldInteraction : MonoBehaviour
    {
        public static WorldInteraction I { get; private set; }

        Transform player;
        Interactable nearest;
        public float range = 3.2f;

        void Awake() => I = this;
        public void SetPlayer(Transform p) => player = p;

        void Update()
        {
            if (player == null) return;

            nearest = null;
            float best = range * range;

            for (int i = Interactable.All.Count - 1; i >= 0; i--)
            {
                var candidate = Interactable.All[i];
                if (candidate == null || !candidate.isActiveAndEnabled) continue;

                float d = (candidate.transform.position - player.position).sqrMagnitude;
                if (d < best)
                {
                    best = d;
                    nearest = candidate;
                }
            }

            HUDController.I?.SetPrompt(nearest == null ? "" : nearest.prompt);
        }

        public void Interact()
        {
            nearest?.Interact();
        }
    }
}
