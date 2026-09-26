using UnityEngine;
using UnityEngine.EventSystems;

namespace MasirServat
{
    public sealed class VirtualJoystick : MonoBehaviour, IPointerDownHandler, IDragHandler, IPointerUpHandler
    {
        public RectTransform knob;
        public float radius = 92f;

        RectTransform baseRect;

        void Awake()
        {
            baseRect = transform as RectTransform;
        }

        public void OnPointerDown(PointerEventData eventData)
        {
            UpdateStick(eventData);
        }

        public void OnDrag(PointerEventData eventData)
        {
            UpdateStick(eventData);
        }

        public void OnPointerUp(PointerEventData eventData)
        {
            MobileInput.Move = Vector2.zero;
            if (knob != null) knob.anchoredPosition = Vector2.zero;
        }

        void UpdateStick(PointerEventData eventData)
        {
            if (baseRect == null || knob == null) return;

            if (!RectTransformUtility.ScreenPointToLocalPointInRectangle(
                    baseRect,
                    eventData.position,
                    eventData.pressEventCamera,
                    out var local))
                return;

            Vector2 clamped = Vector2.ClampMagnitude(local, radius);
            knob.anchoredPosition = clamped;
            MobileInput.Move = Vector2.ClampMagnitude(clamped / radius, 1f);
        }

        void OnDisable()
        {
            MobileInput.Move = Vector2.zero;
            if (knob != null) knob.anchoredPosition = Vector2.zero;
        }
    }
}
