using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using PowerPoint = Microsoft.Office.Interop.PowerPoint;

namespace PPTRemoteServer
{
    class Controller
    {
        public static Boolean isPlaying=false;
        public static PowerPoint.SlideShowWindow show;

        public void startPlay()
        {
            if (isPlaying)
                return;
            else
            {
                PowerPoint.Presentation presentation = Globals.ThisAddIn.Application.ActivePresentation;
                presentation.SlideShowSettings.Run();
                show = presentation.SlideShowWindow;
                isPlaying = true;
            }
        }
        public void endPlay()
        {
            if (!isPlaying)
                return;
            isPlaying = false;
            show.View.Exit();
        }

        public void blacksCreen()
        {
            if (!isPlaying)
                return;
            if (show.View.State == PowerPoint.PpSlideShowState.ppSlideShowRunning)
                show.View.State = PowerPoint.PpSlideShowState.ppSlideShowBlackScreen;
            else
                jumpTo(show.View.Slide.SlideIndex);
        }

        public void whiteCreen()
        {
            if (!isPlaying)
                return;
            if (show.View.State == PowerPoint.PpSlideShowState.ppSlideShowRunning)
                show.View.State = PowerPoint.PpSlideShowState.ppSlideShowWhiteScreen;
            else
                jumpTo(show.View.Slide.SlideIndex);
        }

        public void jumpTo(int index)
        {
            if (isPlaying)
            {
                if (index > 0 && index <=ThisAddIn.totle)
                    show.View.GotoSlide(index);
            }
            else
            {
                if (ThisAddIn.isFileOpen)
                {
                    startPlay();
                    jumpTo(index);
                }
            }
        }
        public void next()
        {
            if (isPlaying)
            {
                show.View.Next();
            }
        }
        public void pre()
        {
            if (isPlaying)
            {
                show.View.Previous();
            }
        }
    }
}
