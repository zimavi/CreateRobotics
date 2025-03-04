package com.workert.robotics.base.roboscript;

import javax.annotation.Nonnull;

public abstract class RoboScript {
	Compiler compiler = new Compiler(this);
	VirtualMachine virtualMachine = new VirtualMachine(this);

	private boolean hadError = false;

	public final void runString(String source) {
		new Thread(() -> {
			this.hadError = false;
			this.compiler = new Compiler(this);
			this.virtualMachine = new VirtualMachine(this);
			this.defineNativeFunctions();
			this.compiler.compile(source);
			if (this.hadError) {
				return;
			}
			this.virtualMachine.interpret(this.compiler.chunk, 0);
		}).start();

	}

	public final void queueSignal(String signalName, Object[] args) {
		ComputerSignal signal = new ComputerSignal(signalName, args);
		this.virtualMachine.addSignalToQueue(signal);
	}

	public final void queueStopForProgram() {
		this.virtualMachine.queueStop();
	}


	protected void defineNativeFunctions() {
		// print(arg)
		this.defineNativeFunction("print", 1, args -> {
			RoboScript.this.handlePrintMessage(RoboScriptArgumentPredicates.stringify(args[0]));
			return null;
		});

		// str(arg)
		this.defineNativeFunction("str", 1, args -> RoboScriptArgumentPredicates.stringify(args[0]));

		this.defineNativeFunction("sin", 1, args -> Math.sin(RoboScriptArgumentPredicates.asNumber(args[0])));
		this.defineNativeFunction("cos", 1, args -> Math.cos(RoboScriptArgumentPredicates.asNumber(args[0])));
		this.defineNativeFunction("tan", 1, args -> Math.tan(RoboScriptArgumentPredicates.asNumber(args[0])));
		this.defineNativeFunction("asin", 1, args -> Math.asin(RoboScriptArgumentPredicates.asNumber(args[0])));
		this.defineNativeFunction("acos", 1, args -> Math.acos(RoboScriptArgumentPredicates.asNumber(args[0])));
		this.defineNativeFunction("atan", 1, args -> Math.atan(RoboScriptArgumentPredicates.asNumber(args[0])));
		this.defineNativeFunction("abs", 1, args -> Math.abs(RoboScriptArgumentPredicates.asNumber(args[0])));
		this.defineNativeFunction("floor", 1, args -> Math.floor(RoboScriptArgumentPredicates.asNumber(args[0])));
		this.defineNativeFunction("ceil", 1, args -> Math.ceil(RoboScriptArgumentPredicates.asNumber(args[0])));
	}

	public final void defineNativeFunction(String name, int argumentCount, NativeFunctionFunctionalInterface function) {
		NativeFunction nativeFunctionWrapper = new NativeFunction() {
			@Override
			Object call(VirtualMachine vm) {
				if (this.argumentCount > 0) {
					Object[] functionArgs = new Object[this.argumentCount];
					for (int i = this.argumentCount - 1; i >= 0; i--) {
						functionArgs[i] = vm.popStack();
					}

					return function.call(functionArgs);
				}
				return function.call(new Object[] {});
			}
		};
		nativeFunctionWrapper.argumentCount = argumentCount;
		this.virtualMachine.nativeFunctions[this.virtualMachine.nativeFunctionSize] = nativeFunctionWrapper;
		this.compiler.nativeFunctionLookup.put(name, (byte) this.virtualMachine.nativeFunctionSize++);
	}

	@FunctionalInterface
	public interface NativeFunctionFunctionalInterface {
		Object call(@Nonnull Object[] parameters);
	}

	protected final void reportCompileError(int line, String message) {
		this.hadError = true;
		this.handleErrorMessage("[line " + line + "] Error: " + message);
	}


	protected abstract void handlePrintMessage(String message);

	protected abstract void handleErrorMessage(String error);


	abstract static class NativeFunction {
		int argumentCount = 0;

		/**
		 * Calls a native function and automatically pushes the return value.
		 *
		 * @param vm The current virtual machine of the executing function.
		 * @return The return value of the function (automatically gets pushed, do not worry about that)
		 */
		abstract Object call(VirtualMachine vm);
	}
}
