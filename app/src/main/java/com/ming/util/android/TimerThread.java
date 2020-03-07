package com.ming.util.android;



public class TimerThread extends Thread
{
	
	private Runnable runnable;
	private long delayMillis;
	private boolean isRuning;
	private boolean isReset;
	private int time;


	public TimerThread(Runnable run, long de)
	{
		this.runnable = run;
		this.delayMillis = de;
	}

	public void setDelay(long mm)
	{
		this.delayMillis = mm;
	}
	
	public void run()
	{
		isRuning = true;
		while(isRuning)
		{
			try
			{
//				Util.debug("TimerThread is Runing time :"+time+"-"+delayMillis+" ^^");
				if(isReset)
				{
//					Util.debug("-----------------------reset()");
					time = 0;
					isReset = false;
					
				}
				if(time%delayMillis==0)
				{
//					Util.debug("-----------------------run()");
					runnable.run();
				}
				time++;
				if(time>=delayMillis)
				{
					isReset = true;
				}
				if(ls!=null)
				{
					ls.run(time);
//					if(time%2==0)
//					{
//					}
				}
				Thread.sleep(1000);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}


	public void close()
	{
		isRuning = false;
	}

	public void reset() {
		isReset = true;
	}
	
	public void start()
	{
		if(!isRuning)
		{
			try
			{
				super.start();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			reset();
		}
	}

	public void pause() {
		isRuning = false;
		// TODO Auto-generated method stub
		
	}

	private RunningListener ls;
	public void setRuningListener(RunningListener ls) {
		// TODO Auto-generated method stub
		this.ls = ls;
		
	}


}
