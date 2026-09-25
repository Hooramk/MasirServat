using UnityEngine;

namespace MasirServat
{
    public sealed class VehicleMount : MonoBehaviour
    {
        Collider hitCollider;
        Vector3 originalScale;

        void Awake()
        {
            hitCollider = GetComponent<Collider>();
            originalScale = transform.localScale;
            RefreshVisual();
        }

        public void RefreshVisual()
        {
            var renderer = GetComponentInChildren<Renderer>();
            if (renderer != null)
                renderer.material.color = GameState.I != null && GameState.I.Data.hasScooter
                    ? new Color(0.08f, 0.35f, 0.65f)
                    : new Color(0.42f, 0.44f, 0.46f);
        }

        public void Use()
        {
            var player = PlayerMotor.I;
            if (player == null) return;

            if (!GameState.I.Data.hasScooter)
            {
                if (!EconomySystem.I.BuyScooter())
                {
                    HUDController.I?.Toast("قیمت موتور: ۹ میلیون تومان");
                    return;
                }
                RefreshVisual();
                HUDController.I?.Toast("موتور خریدی! حالا دوباره بزن و سوار شو");
                return;
            }

            if (player.IsMounted) player.Dismount();
            else player.Mount(this);
        }

        public void AttachTo(Transform player)
        {
            if (hitCollider != null) hitCollider.enabled = false;
            transform.SetParent(player);
            transform.localPosition = new Vector3(0, -0.75f, 0.1f);
            transform.localRotation = Quaternion.identity;
            transform.localScale = originalScale;
        }

        public void DetachAt(Vector3 worldPosition)
        {
            transform.SetParent(null);
            transform.position = new Vector3(worldPosition.x, 0.45f, worldPosition.z);
            transform.rotation = Quaternion.identity;
            transform.localScale = originalScale;
            if (hitCollider != null) hitCollider.enabled = true;
        }
    }
}
