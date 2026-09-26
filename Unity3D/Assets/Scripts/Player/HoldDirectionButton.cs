using UnityEngine;
using UnityEngine.EventSystems;

namespace MasirServat
{
    public sealed class HoldDirectionButton : MonoBehaviour, IPointerDownHandler, IPointerUpHandler, IPointerExitHandler
    {
        public Vector2 direction;

        public void OnPointerDown(PointerEventData eventData) => MobileInput.Move = direction;
        public void OnPointerUp(PointerEventData eventData) => MobileInput.Move = Vector2.zero;
        public void OnPointerExit(PointerEventData eventData) => MobileInput.Move = Vector2.zero;

        void OnDisable()
        {
            if (MobileInput.Move == direction) MobileInput.Move = Vector2.zero;
        }
    }
}
