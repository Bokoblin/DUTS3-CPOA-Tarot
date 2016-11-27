/*
Copyright 2016 Jacquot Alexandre, Jolivet Arthur S3A
Licensed under the Apache License, Version 2.0 (the "License";
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package app.model;

/**
 * The {@code Bids} enumeration defines
 * all bids that are possible
 * @author Arthur
 * @version v0.8.2
 * @since v0.6
 */
public enum Bids {
    Small,
    Guard,
    GuardWithoutTheKitty,
    GuardAgainstTheKitty,
    Pass;

    /**
     * Gets Bids value from ordinal
     * @since v0.8.2
     *
     * @return the corresponding bids value
     */
    public static Bids valueOf(int ordinal) throws Exception {
        Bids result;
        switch (ordinal) {
            case 1:
                result = Bids.Small;
                break;
            case 2:
                result = Bids.Guard;
                break;
            case 3:
                result = Bids.GuardWithoutTheKitty;
                break;
            case 4:
                result = Bids.GuardAgainstTheKitty;
                break;
            case 5:
                result = Bids.Pass;
                break;
            default:
                throw new Exception("No Bid corresponds to this number");
        }
        return result;
    }
}
