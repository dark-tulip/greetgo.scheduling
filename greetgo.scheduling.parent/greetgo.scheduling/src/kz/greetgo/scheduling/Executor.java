package kz.greetgo.scheduling;

public class Executor {

  private final Object sync = new Object();

  private final String threadName;

  public Executor(String threadName) {
    this.threadName = threadName;
  }

  private volatile boolean working = false;

  private class LocalThread extends Thread {

    public LocalThread() {
      super(threadName);
    }

    @Override
    public void run() {
      while (Thread.currentThread() == workingThread) {

        working = true;
        currentTask.run();
        working = false;

        lastFinishMoment = System.currentTimeMillis();

        synchronized (sync) {
          try {
            sync.wait();
          } catch (InterruptedException e) {
            return;
          }
        }

      }
    }
  }

  private volatile LocalThread workingThread = null;

  public boolean working() {
    return working;
  }

  private volatile Task currentTask;

  public void startExecution(Task task) {
    currentTask = task;
    if (workingThread == null) {
      workingThread = new LocalThread();
      workingThread.start();
    } else {
      synchronized (sync) {
        sync.notifyAll();
      }
    }
  }

  public Task currentTask() {
    return currentTask;
  }

  public boolean hasWorkingThread() {
    return workingThread != null;
  }

  private volatile long lastFinishMoment;

  public long getLastFinishMoment() {
    return lastFinishMoment;
  }

  public void stopThread() {
    workingThread = null;
    synchronized (sync) {
      sync.notifyAll();
    }
  }
}
