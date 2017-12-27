package kz.greetgo.scheduling;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class Executor {

  private final Object sync = new Object();

  private final String threadName;

  public Executor(String threadName) {
    this.threadName = threadName;
  }

  private final AtomicBoolean working = new AtomicBoolean(false);

  private class LocalThread extends Thread {

    public LocalThread() {
      super(threadName);
    }

    @Override
    public void run() {
      while (Thread.currentThread() == workingThread.get() || !tasksToRun.isEmpty()) {

        if (!tasksToRun.isEmpty()) {
          working.set(true);
          while (true) {
            Task task = tasksToRun.poll();
            if (task == null) break;
            task.run();
          }
          working.set(false);
        }

        lastFinishMoment.set(System.currentTimeMillis());

        synchronized (sync) {
          try {
            sync.wait();
          } catch (InterruptedException e) {
            workingThread.set(null);
          }
        }

      }
    }
  }

  private final AtomicReference<LocalThread> workingThread = new AtomicReference<>(null);

  public boolean working() {
    return working.get();
  }

  private final ConcurrentLinkedQueue<Task> tasksToRun = new ConcurrentLinkedQueue<>();

  public void startExecution(Task task) {
    tasksToRun.add(task);
    if (workingThread.get() == null) {
      workingThread.set(new LocalThread());
      workingThread.get().start();
    } else {
      synchronized (sync) {
        sync.notifyAll();
      }
    }
  }

  public boolean hasWorkingThread() {
    return workingThread.get() != null;
  }

  private final AtomicLong lastFinishMoment = new AtomicLong(0);

  /**
   * This method need for Execution pool to stop long running threads
   */
  public long getLastFinishMoment() {
    return lastFinishMoment.get();
  }

  public void stopThread() {
    workingThread.set(null);
    synchronized (sync) {
      sync.notifyAll();
    }
  }
}
