package de.wegmann.convert

/**
 * User: andy
 * Date: 03.09.13
 */
abstract class AbstractToolCommand implements ToolCommandIF {
    boolean dryRun

    @Override
    void setDryRun(boolean dryRun) {
        this.dryRun = dryRun
    }

    @Override
    boolean isDryRun() {
        return this.dryRun
    }
}
